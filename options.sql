-- 1 Query.
SELECT c.cname as customer, b.title AS discounted_title, d.amount AS discount_amount
    FROM customer as c
    INNER JOIN wants as w ON c.cid = w.cid
    INNER JOIN discounts as d ON d.isbn = w.isbn
    INNER JOIN books as b ON w.isbn = b.isbn
    WHERE d.until > current_date
    ORDER BY b.title;

-- 2 Publishers contact info when books are out of stock
SELECT p.pname AS publisher, p.phone_number, p.address, count(*) AS books_to_order
    FROM publisher as p
    INNER JOIN publishes as pb ON p.pid = pb.pid
    INNER JOIN (SELECT isbn FROM books where qty_stock = 0) as no_stock ON pb.isbn = no_stock.isbn
    GROUP BY p.pname, p.phone_number, p.address;

-- 3 Modification.
-- Increase the price for a given publisher.
UPDATE books as B
SET price = B.price + ?
WHERE B.isbn IN (SELECT P2.isbn
	FROM publisher P
	INNER JOIN publishes P2
	ON P.pid = P2.pid AND P.pname = ?);

-- 4 Modfication:
-- Clenup publishers list
DELETE FROM publisher	P
NOT EXISTS (SELECT * FROM publishes P2
			WHERE P.pid = P2.pid);

-- 5 Isert
-- add a new publisher
INSERT INTO publisher VALUES(1, 'Penguin Books', '1112223333', 'A place in the USA');
