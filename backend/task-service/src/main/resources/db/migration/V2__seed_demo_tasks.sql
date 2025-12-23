insert into task_schema.tasks (id, board_id, column_id, title, description, status, assignee_id, labels, created_at, updated_at) values
  ('task-1', 'demo-board', 'todo',  'Préparer la démo',  'Lister les fonctionnalités à montrer', 'todo',  'demo', 'demo,prep', now(), now()),
  ('task-2', 'demo-board', 'doing', 'Mettre à jour le README', 'Ajouter la section OIDC/Keycloak', 'doing', 'demo', 'docs', now(), now()),
  ('task-3', 'demo-board', 'done',  'Configurer Keycloak', 'Certificat auto-signé et clients Kanban', 'done', 'demo', 'auth', now(), now());
