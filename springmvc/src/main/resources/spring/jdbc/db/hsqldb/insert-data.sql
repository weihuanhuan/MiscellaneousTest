-- default data
INSERT INTO redis
(name, group_name, mode, role, master_name, node, config_template, config_text, install_path)
VALUES
('default-redis-1', 'default-group-1', 'cluster', 'master', NULL, 'default-node-1', 'default-temp-1', 'default-config-1', 'default-path-1');

INSERT INTO redis
(name, group_name, mode, role, master_name, node, config_template, config_text, install_path)
VALUES
('default-redis-2', 'default-group-1', 'cluster', 'replica', 'default-redis-1', 'default-node-1', 'default-temp-1', 'default-config-2', 'default-path-2');
