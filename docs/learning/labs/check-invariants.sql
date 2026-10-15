-- Read-only diagnostics. Run against an isolated test database.
-- Compare results with the intended business rules before drawing conclusions.
USE ticket_boot;

-- One active reservation per session and resource: expected no rows.
SELECT session_id, seat_id, COUNT(*) AS active_count
FROM order_item
WHERE active_flag = 1 AND deleted = 0
GROUP BY session_id, seat_id
HAVING COUNT(*) > 1;

-- Session counter versus committed active items: inspect mismatches.
SELECT s.id, s.total_stock, s.available_stock,
       COALESCE(i.active_count, 0) AS active_count,
       s.total_stock - COALESCE(i.active_count, 0) AS expected_available
FROM `session` s
LEFT JOIN (
    SELECT session_id, COUNT(*) AS active_count
    FROM order_item WHERE active_flag = 1 AND deleted = 0
    GROUP BY session_id
) i ON i.session_id = s.id
WHERE s.deleted = 0
  AND s.available_stock <> s.total_stock - COALESCE(i.active_count, 0);

-- Cancelled orders should not keep active resources: expected no rows.
SELECT o.id, o.status, COUNT(*) AS active_count
FROM reservation_order o
JOIN order_item i ON i.order_id = o.id AND i.active_flag = 1 AND i.deleted = 0
WHERE o.status = 'CANCELLED' AND o.deleted = 0
GROUP BY o.id, o.status;

-- Like counter versus active like facts: inspect mismatches.
SELECT r.id, r.like_count, COALESCE(l.active_likes, 0) AS expected_likes
FROM review r
LEFT JOIN (
    SELECT review_id, COUNT(*) AS active_likes
    FROM review_like WHERE active_flag = 1 AND deleted = 0
    GROUP BY review_id
) l ON l.review_id = r.id
WHERE r.deleted = 0 AND r.active_flag = 1
  AND r.like_count <> COALESCE(l.active_likes, 0);

-- Expired pending orders: delay is evidence, not automatically corruption.
SELECT id, order_no, expire_time,
       TIMESTAMPDIFF(SECOND, expire_time, NOW()) AS overdue_seconds
FROM reservation_order
WHERE status = 'PENDING' AND expire_time < NOW() AND deleted = 0
ORDER BY expire_time, id LIMIT 100;
