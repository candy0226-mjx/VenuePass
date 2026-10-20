"""Maintain the VenuePass learning question bank with Python's standard library."""

import argparse
import copy
import json
import os
from pathlib import Path
import re
import shutil
import sys
import tempfile
from datetime import date, datetime, timezone
from urllib.parse import unquote


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_BANK = ROOT / "docs" / "learning" / "questions.json"
FIELDS = {
    "id", "topic", "priority", "question", "answer", "followups",
    "code_refs", "source_ids", "status", "notes",
}


def atomic_write(path, text):
    path.parent.mkdir(parents=True, exist_ok=True)
    temp_path = None
    try:
        with tempfile.NamedTemporaryFile(
            mode="w", encoding="utf-8", newline="\n", dir=path.parent,
            prefix=".study-", suffix=".tmp", delete=False,
        ) as handle:
            temp_path = Path(handle.name)
            handle.write(text)
        os.replace(temp_path, path)
    finally:
        if temp_path is not None and temp_path.exists():
            temp_path.unlink()


class StudyStore:
    def __init__(self, bank=DEFAULT_BANK, root=ROOT):
        self.bank = Path(bank).resolve()
        self.root = Path(root).resolve()
        self.learning = self.root / "docs" / "learning"
        self.output = self.bank.with_name("interview-questions.md")

    def load(self):
        data = json.loads(self.bank.read_text(encoding="utf-8-sig"))
        self.validate(data)
        return data

    def validate(self, data):
        if not isinstance(data, dict) or data.get("schema_version") != 1:
            raise ValueError("Expected schema_version 1")
        if not isinstance(data.get("questions"), list):
            raise ValueError("questions must be an array")
        sources = (self.learning / "sources.md").read_text(encoding="utf-8")
        known_sources = {item.upper() for item in re.findall(r'id="([sc]\d{2})"', sources)}
        seen = set()
        for card in data["questions"]:
            if not isinstance(card, dict) or set(card) != FIELDS:
                raise ValueError("Question fields must match the documented schema")
            identity = card["id"]
            if not isinstance(identity, str) or not re.fullmatch(r"Q\d{3}", identity):
                raise ValueError("Question id must look like Q001")
            if identity in seen:
                raise ValueError(f"Duplicate question: {identity}")
            seen.add(identity)
            for field in ("topic", "question", "answer"):
                if not isinstance(card[field], str) or not card[field].strip():
                    raise ValueError(f"{identity}: {field} must be nonempty text")
            if card["priority"] not in ("P0", "P1", "P2"):
                raise ValueError(f"{identity}: invalid priority")
            if card["status"] not in ("todo", "learning", "mastered"):
                raise ValueError(f"{identity}: invalid status")
            if not isinstance(card["notes"], str):
                raise ValueError(f"{identity}: notes must be text")
            for field in ("followups", "code_refs", "source_ids"):
                if not isinstance(card[field], list) or not all(
                    isinstance(item, str) and item.strip() for item in card[field]
                ):
                    raise ValueError(f"{identity}: {field} must contain text items")
            for source in card["source_ids"]:
                if source not in known_sources:
                    raise ValueError(f"{identity}: unknown source {source}")
            for reference in card["code_refs"]:
                file_name, _, symbol = reference.partition("::")
                target = (self.root / file_name).resolve()
                if not target.is_relative_to(self.root) or not target.is_file():
                    raise ValueError(f"{identity}: missing or out-of-root code reference {file_name}")
                if symbol and symbol not in target.read_text(encoding="utf-8-sig"):
                    raise ValueError(f"{identity}: missing symbol {symbol} in {file_name}")

    @staticmethod
    def render(data):
        cards = sorted(data["questions"], key=lambda item: item["id"])
        lines = [
            "# VenuePass 面试题集", "",
            "本文件由 `python tools/study.py export` 生成。请编辑 questions.json 或使用维护工具。", "",
            "按“面试官提问 → 我回答 → 两轮追问与回答”阅读。回答是口语示范，请结合自己的理解和真实经历表达，不要把改进方案说成已实现。", "",
            "源码核对与修订日期见 questions.json 的 updated_at；原理来源见 sources.md。优先级为学习顺序，不代表真实面试概率。", "",
            f"共 {len(cards)} 题。状态：todo 未学 / learning 学习中 / mastered 能脱稿讲解。", "",
            "[学习目录](README.md) · [引用来源](sources.md) · [当前实现边界](12-implementation-boundaries.md)", "",
            "| 编号 | 主题 | 优先级 | 状态 | 题目 |", "|---|---|---|---|---|",
        ]
        for card in cards:
            title = card["question"].replace("|", "\\|").replace("\n", " ")
            lines.append(
                f'| [{card["id"]}](#{card["id"].lower()}) | {card["topic"]} | '
                f'{card["priority"]} | {card["status"]} | {title} |'
            )
        for card in cards:
            lines.extend([
                "", f'<a id="{card["id"].lower()}"></a>',
                f'## {card["id"]} {card["question"]}', "",
                f'主题：{card["topic"]}；优先级：{card["priority"]}；状态：{card["status"]}。', "",
                f'**面试官：** {card["question"]}', "",
                "**我：**", "", card["answer"], "",
            ])
            for index, item in enumerate(card["followups"], start=1):
                lines.extend([f"**面试官追问 {index}：** {item}", ""])
            if card["code_refs"]:
                lines.extend(["", "**源码入口**", ""])
                for reference in card["code_refs"]:
                    file_name, _, symbol = reference.partition("::")
                    suffix = f"：`{symbol}`" if symbol else ""
                    lines.append(f"- [{Path(file_name).name}](../../{file_name}){suffix}")
            if card["source_ids"]:
                links = "、".join(f"[{item}](sources.md#{item.lower()})" for item in card["source_ids"])
                lines.extend(["", f"原理或主题来源：{links}。"])
            lines.extend(["", "**我的笔记**", "", card["notes"] or "待补充自己的推演、实验记录和回答。"])
        return "\n".join(lines) + "\n"

    def export(self, data=None):
        data = self.load() if data is None else data
        self.validate(data)
        atomic_write(self.output, self.render(data))

    def save(self, data):
        self.validate(data)
        data["updated_at"] = date.today().isoformat()
        backup_dir = self.root / "dist" / "learning-backups"
        backup_dir.mkdir(parents=True, exist_ok=True)
        if self.bank.exists():
            stamp = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%S%fZ")
            shutil.copy2(self.bank, backup_dir / f"questions-{stamp}.json")
        atomic_write(self.bank, json.dumps(data, ensure_ascii=False, indent=2) + "\n")
        self.export(data)

    def add(self, card):
        data = self.load()
        card = copy.deepcopy(card)
        card.setdefault("status", "todo")
        card.setdefault("notes", "")
        for field in ("followups", "code_refs", "source_ids"):
            card.setdefault(field, [])
        data["questions"].append(card)
        self.save(data)

    def update(self, identity, changes):
        if not changes or set(changes) - (FIELDS - {"id"}):
            raise ValueError("Supply supported fields; id cannot be changed")
        data = self.load()
        card = self.find(data, identity)
        card.update(changes)
        self.save(data)

    def delete(self, identity):
        data = self.load()
        self.find(data, identity)
        data["questions"] = [item for item in data["questions"] if item["id"] != identity]
        self.save(data)

    @staticmethod
    def find(data, identity):
        for card in data["questions"]:
            if card["id"] == identity:
                return card
        raise ValueError(f"Unknown question: {identity}")

    def check(self):
        data = self.load()
        if not self.output.exists() or self.output.read_text(encoding="utf-8") != self.render(data):
            raise ValueError("Generated question document is stale; run export")
        files = list(self.learning.rglob("*.md")) + [self.root / "README.md"]
        link_count = 0
        for document in files:
            if not document.exists():
                continue
            text = document.read_text(encoding="utf-8")
            if len(re.findall(r"^```", text, flags=re.M)) % 2:
                raise ValueError(f"Unclosed fenced code block: {document}")
            prose = re.sub(r"^```.*?^```[^\n]*", "", text, flags=re.M | re.S)
            for address in re.findall(r"\[[^\]]*\]\(([^\s)]+)\)", prose):
                if re.match(r"^[a-zA-Z][a-zA-Z0-9+.-]*:", address):
                    continue
                relative, _, fragment = unquote(address).partition("#")
                target = (document.parent / relative).resolve() if relative else document.resolve()
                if not target.is_relative_to(self.root) or not target.exists():
                    raise ValueError(f"Broken local link in {document.name}: {address}")
                if fragment and target.suffix == ".md":
                    destination = target.read_text(encoding="utf-8")
                    explicit = re.findall(r'<a id="([^"]+)"', destination)
                    headings = re.findall(r"^#{1,6}\s+(.+)$", destination, flags=re.M)
                    slugs = {
                        re.sub(r"[^\w\-\s]", "", heading.lower()).replace(" ", "-")
                        for heading in headings
                    }
                    if fragment not in set(explicit) | slugs:
                        raise ValueError(f"Broken anchor in {document.name}: {address}")
                link_count += 1
        return len(data["questions"]), len(files), link_count


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--bank", type=Path, default=DEFAULT_BANK)
    commands = parser.add_subparsers(dest="command", required=True)
    listing = commands.add_parser("list")
    listing.add_argument("--topic")
    listing.add_argument("--priority", choices=("P0", "P1", "P2"))
    listing.add_argument("--status", choices=("todo", "learning", "mastered"))
    search = commands.add_parser("search")
    search.add_argument("text")
    show = commands.add_parser("show")
    show.add_argument("id")
    add = commands.add_parser("add")
    add.add_argument("--input", required=True, type=Path)
    update = commands.add_parser("update")
    update.add_argument("id")
    update.add_argument("--input", type=Path)
    update.add_argument("--status", choices=("todo", "learning", "mastered"))
    update.add_argument("--notes")
    delete = commands.add_parser("delete")
    delete.add_argument("id")
    delete.add_argument("--yes", action="store_true")
    commands.add_parser("export")
    commands.add_parser("check")
    args = parser.parse_args(argv)
    store = StudyStore(args.bank)
    try:
        if args.command in ("list", "search", "show"):
            data = store.load()
            if args.command == "show":
                print(json.dumps(store.find(data, args.id), ensure_ascii=False, indent=2))
                return 0
            cards = sorted(data["questions"], key=lambda item: item["id"])
            if args.command == "search":
                needle = args.text.casefold()
                cards = [card for card in cards if needle in json.dumps(card, ensure_ascii=False).casefold()]
            else:
                for field in ("topic", "priority", "status"):
                    value = getattr(args, field)
                    if value:
                        cards = [card for card in cards if value.casefold() in card[field].casefold()]
            for card in cards:
                print(f'{card["id"]} [{card["priority"]}/{card["status"]}] {card["topic"]}: {card["question"]}')
            print(f"Total: {len(cards)}")
        elif args.command == "add":
            store.add(json.loads(args.input.read_text(encoding="utf-8-sig")))
            print("Added; previous bank backed up and reading view regenerated.")
        elif args.command == "update":
            changes = json.loads(args.input.read_text(encoding="utf-8-sig")) if args.input else {}
            if not isinstance(changes, dict):
                raise ValueError("Update input must be a JSON object")
            if args.status:
                changes["status"] = args.status
            if args.notes is not None:
                changes["notes"] = args.notes
            store.update(args.id, changes)
            print("Updated; previous bank backed up and reading view regenerated.")
        elif args.command == "delete":
            if not args.yes:
                raise ValueError("Deletion requires --yes; no files changed")
            store.delete(args.id)
            print("Deleted; previous bank backed up and reading view regenerated.")
        elif args.command == "export":
            store.export()
            print(f"Generated: {store.output}")
        else:
            cards, files, links = store.check()
            print(f"OK: {cards} questions, {files} Markdown files, {links} local links checked.")
        return 0
    except (OSError, ValueError, TypeError, KeyError) as error:
        print(f"Error: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
        sys.stderr.reconfigure(encoding="utf-8")
    raise SystemExit(main())
