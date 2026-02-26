CREATE TABLE `c2_role_x_permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `roleId` int NOT NULL,
  `permissionId` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Role_x_Permission_role` (`roleId`)
) ENGINE = InnoDB AUTO_INCREMENT = 4117 DEFAULT CHARSET = utf8mb3;


CREATE TABLE `c2_materials_categories` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(128) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `c2_materials_subcategories` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(128) NOT NULL,
  `category` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `c2_materials_materials` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(255) NOT NULL,
  `aliases` varchar(516) DEFAULT NULL,
  `location` varchar(128) NOT NULL,
  `cost` decimal(11, 2) DEFAULT NULL,
  `category` int NOT NULL,
  `subcategory` int NOT NULL,
  `photo` int DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `approved` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 15 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `c2_materials_orders` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `urgent` boolean NOT NULL,
  `cost` decimal(11, 2) DEFAULT NULL,
  `site` int ,
  `user` int NOT NULL,
  `state` varchar(16) NOT NULL,
  `requested` timestamp default null,
  `relatedOrder` int,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE `c2_materials_orders_lines` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `parent` int NOT NULL,
  `material` int NOT NULL,
  `urgent` boolean NOT NULL,
  `state` varchar(16) NOT NULL,
  `comment` varchar(256) DEFAULT NULL,
  `quantity` int NOT NULL,
  `uuid` varchar(64) default null,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `c2_user` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(128) NOT NULL,
  `lastname` varchar(256) NOT NULL,
  `login` varchar(32) NOT NULL,
  `password` varchar(128) NOT NULL,
  `active` boolean DEFAULT true,
  `homepage` varchar(128) DEFAULT NULL,
  `role` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE `c2_sites` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(256) NOT NULL,
  `address` varchar(512) DEFAULT NULL,
  `city` varchar(128) DEFAULT NULL,
  `province` varchar(128) DEFAULT NULL,
  `postalCode` varchar(32) DEFAULT NULL,
  `active` boolean DEFAULT true,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `c2_roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 13 DEFAULT CHARSET = utf8mb3;


CREATE TABLE `c2_files` (
  `id` int NOT NULL AUTO_INCREMENT,
  `path` varchar(2048) NOT NULL,
  `name` varchar(512) NOT NULL,
  `view` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 298 DEFAULT CHARSET = utf8mb3;

insert into `c2_roles` (`name`) values ('Administrador');
insert into `c2_role_x_permission` (`permissionId`, `roleId`) values 
  ('webmaster.roles.write', 1), ('webmaster.users.write', 1),
  ('admin.sites.write', 1),
  ('materiales.admin.write', 1), ('materiales.shop.write', 1), ('materiales.order.processing.write', 1), 
   ('materiales.approve.write', 1), 
  ('materiales.orders.admin.write', 1);
insert into `c2_user` (`homepage`, `login`, `name`, `lastname`,  `password`, `active`, `role`) values ('materials-shop', 'edu', 'Eduardo', 'Rodríguez Lorenzo',
  '6fb578a51128ae8916adea3c7de3747024d377c0', 1, 1);


