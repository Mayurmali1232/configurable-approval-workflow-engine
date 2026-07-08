DELETE FROM approval_history;
DELETE FROM approval_steps;
DELETE FROM requests;
DELETE FROM users;

-- Seed ONLY Workflow Path configurations
INSERT INTO approval_steps (id, request_type, step_order, role) VALUES (1, 'LEAVE', 1, 'APPROVER');
INSERT INTO approval_steps (id, request_type, step_order, role) VALUES (2, 'LEAVE', 2, 'ADMIN');
INSERT INTO approval_steps (id, request_type, step_order, role) VALUES (3, 'EXPENSE', 1, 'APPROVER');
INSERT INTO approval_steps (id, request_type, step_order, role) VALUES (4, 'EXPENSE', 2, 'APPROVER');
INSERT INTO approval_steps (id, request_type, step_order, role) VALUES (5, 'EXPENSE', 3, 'ADMIN');

ALTER TABLE approval_steps ALTER COLUMN id RESTART WITH 6;