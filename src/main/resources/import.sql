INSERT INTO medico (id, nome, crm) VALUES (1, 'Dr. João Silva', 'CRM1234');
INSERT INTO medico (id, nome, crm) VALUES (2, 'Dra. Maria Oliveira', 'CRM5678');

INSERT INTO paciente (id, nome, telefone) VALUES (4, 'Carlos Andrade', '6399991001');
INSERT INTO paciente (id, nome, telefone) VALUES (5, 'Fernanda Souza', '6399991002');
INSERT INTO paciente (id, nome, telefone) VALUES (6, 'Ricardo Lima', '6399991003');
INSERT INTO paciente (id, nome, telefone) VALUES (7, 'Ana Paula Mendes', '6399991004');
INSERT INTO paciente (id, nome, telefone) VALUES (8, 'Eduardo Castro', '6399991005');
INSERT INTO paciente (id, nome, telefone) VALUES (9, 'Beatriz Rocha', '6399991006');


INSERT INTO agenda (id, inicio, fim, duracao, status, medico_id) VALUES (1, '2025-06-10T08:00:00', '2025-06-10T08:30:00', 30, 'DISPONIVEL', 2);
INSERT INTO agenda (id, inicio, fim, duracao, status, medico_id) VALUES (2, '2025-06-10T08:30:00', '2025-06-10T09:00:00', 30, 'DISPONIVEL', 2);
INSERT INTO agenda (id, inicio, fim, duracao, status, medico_id) VALUES (3, '2025-06-10T09:00:00', '2025-06-10T09:30:00', 30, 'DISPONIVEL', 2);
INSERT INTO agenda (id, inicio, fim, duracao, status, medico_id) VALUES (4, '2025-06-10T09:30:00', '2025-06-10T10:00:00', 30, 'DISPONIVEL', 2);
INSERT INTO agenda (id, inicio, fim, duracao, status, medico_id) VALUES (5, '2025-06-10T10:00:00', '2025-06-10T10:30:00', 30, 'DISPONIVEL', 2);
INSERT INTO agenda (id, inicio, fim, duracao, status, medico_id) VALUES (6, '2025-06-10T10:30:00', '2025-06-10T11:00:00', 30, 'DISPONIVEL', 2);
INSERT INTO agenda (id, inicio, fim, duracao, status, medico_id) VALUES (7, '2025-06-10T11:00:00', '2025-06-10T11:30:00', 30, 'DISPONIVEL', 2);
INSERT INTO agenda (id, inicio, fim, duracao, status, medico_id) VALUES (8, '2025-06-10T11:30:00', '2025-06-10T12:00:00', 30, 'DISPONIVEL', 2);
ALTER TABLE agenda ALTER COLUMN id RESTART WITH 9;