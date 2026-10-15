"""Focused persistence, validation, and link tests for the learning-bank tool."""

import copy
import json
from pathlib import Path
import tempfile
import unittest

from study import StudyStore


class StudyStoreTests(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.root = Path(self.directory.name)
        self.learning = self.root / "docs" / "learning"
        self.learning.mkdir(parents=True)
        (self.learning / "sources.md").write_text('<a id="s01"></a>\n', encoding="utf-8")
        (self.learning / "README.md").write_text("# Index\n", encoding="utf-8")
        (self.learning / "12-implementation-boundaries.md").write_text("# Boundaries\n", encoding="utf-8")
        (self.root / "Example.java").write_text("void createOrder() {}", encoding="utf-8")
        (self.root / "README.md").write_text("# Example\n", encoding="utf-8")
        self.card = {
            "id": "Q001", "topic": "事务", "priority": "P0",
            "question": "为什么？", "answer": "说明实际边界。",
            "followups": ["如何验证？"], "code_refs": ["Example.java::createOrder"],
            "source_ids": ["S01"], "status": "todo", "notes": "",
        }
        self.bank = self.learning / "questions.json"
        self.bank.write_text(json.dumps({
            "schema_version": 1, "questions": [self.card],
        }, ensure_ascii=False), encoding="utf-8")
        self.store = StudyStore(self.bank, self.root)
        self.store.export()

    def test_unicode_round_trip_and_generated_links(self):
        self.assertEqual(self.store.load()["questions"][0]["topic"], "事务")
        count, _, links = self.store.check()
        self.assertEqual(count, 1)
        self.assertGreater(links, 0)
        self.assertIn("说明实际边界", self.store.output.read_text(encoding="utf-8"))

    def test_add_update_delete_are_persistent_and_backed_up(self):
        card = copy.deepcopy(self.card)
        card["id"] = "Q002"
        self.store.add(card)
        self.store.update("Q002", {"status": "mastered", "notes": "已画时序"})
        self.assertEqual(self.store.find(self.store.load(), "Q002")["notes"], "已画时序")
        self.store.delete("Q002")
        self.assertEqual(len(self.store.load()["questions"]), 1)
        self.assertEqual(len(list((self.root / "dist" / "learning-backups").glob("*.json"))), 3)

    def test_duplicate_add_does_not_change_bank(self):
        before = self.bank.read_bytes()
        with self.assertRaisesRegex(ValueError, "Duplicate"):
            self.store.add(copy.deepcopy(self.card))
        self.assertEqual(self.bank.read_bytes(), before)

    def test_invalid_update_does_not_change_bank(self):
        before = self.bank.read_bytes()
        with self.assertRaisesRegex(ValueError, "invalid status"):
            self.store.update("Q001", {"status": "invented"})
        self.assertEqual(self.bank.read_bytes(), before)

    def test_unknown_source_is_rejected(self):
        with self.assertRaisesRegex(ValueError, "unknown source"):
            self.store.update("Q001", {"source_ids": ["S99"]})

    def test_outside_code_reference_is_rejected(self):
        with self.assertRaisesRegex(ValueError, "out-of-root"):
            self.store.update("Q001", {"code_refs": ["../private.txt"]})

    def test_missing_symbol_is_rejected(self):
        with self.assertRaisesRegex(ValueError, "missing symbol"):
            self.store.update("Q001", {"code_refs": ["Example.java::missingMethod"]})

    def test_unknown_id_and_id_reassignment_are_rejected(self):
        with self.assertRaisesRegex(ValueError, "Unknown question"):
            self.store.delete("Q999")
        with self.assertRaisesRegex(ValueError, "id cannot"):
            self.store.update("Q001", {"id": "Q002"})

    def test_stale_generated_view_is_detected(self):
        self.store.output.write_text("outdated", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "stale"):
            self.store.check()

    def test_broken_link_is_detected(self):
        (self.learning / "broken.md").write_text("[missing](missing.md)", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "Broken local link"):
            self.store.check()


if __name__ == "__main__":
    unittest.main()
