-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema dpp_resources
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `dpp_resources` ;

-- -----------------------------------------------------
-- Schema dpp_resources
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dpp_resources` DEFAULT CHARACTER SET latin1 ;
USE `dpp_resources` ;

-- -----------------------------------------------------
-- Table `dpp_resources`.`ami_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`ami_info` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`ami_info` (
  `service_name` VARCHAR(45) NULL DEFAULT NULL,
  `ami_id` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dpp_resources`.`candidate_instances`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`candidate_instances` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`candidate_instances` (
  `instance_type_id` INT(11) NOT NULL AUTO_INCREMENT,
  `instance_type` VARCHAR(45) NOT NULL,
  `availability_zone` VARCHAR(45) NULL DEFAULT NULL,
  `price_per_hour` FLOAT NULL DEFAULT NULL,
  PRIMARY KEY (`instance_type_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dpp_resources`.`dpp_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`dpp_info` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`dpp_info` (
  `dpp_id` INT(11) NOT NULL,
  `app_name` VARCHAR(45) NULL DEFAULT NULL,
  `ingestion_service` VARCHAR(45) NULL DEFAULT NULL,
  `processing_service` VARCHAR(45) NULL DEFAULT NULL,
  `storage_service` VARCHAR(45) NULL DEFAULT NULL,
  `ingestion_rate` INT(11) NULL DEFAULT NULL,
  `e2e_latency` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`dpp_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dpp_resources`.`ingestion_cluster_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`ingestion_cluster_info` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`ingestion_cluster_info` (
  `cluster_id` INT(11) NOT NULL,
  `no_of_nodes` INT(11) NULL DEFAULT NULL,
  `instance_type` VARCHAR(128) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `replication_factor` INT(11) NULL DEFAULT NULL,
  `partitions_count` INT(11) NULL DEFAULT NULL,
  `topic_name` VARCHAR(45) NULL DEFAULT NULL,
  `zk_dnsname` VARCHAR(128) NULL DEFAULT NULL,
  `throughput` INT(11) NULL DEFAULT NULL,
  `latency` INT(11) NULL DEFAULT NULL,
  `data_ingestion_rate` VARCHAR(45) NULL DEFAULT NULL,
  `zk_instance_id` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`cluster_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dpp_resources`.`ingestion_nodes_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`ingestion_nodes_info` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`ingestion_nodes_info` (
  `instance_id` VARCHAR(56) NOT NULL,
  `instance_type` VARCHAR(45) NULL DEFAULT NULL,
  `availability_zone` VARCHAR(45) NULL DEFAULT NULL,
  `public_dnsname` VARCHAR(128) NULL DEFAULT NULL,
  `public_ip` VARCHAR(45) NULL DEFAULT NULL,
  `status` VARCHAR(45) NULL DEFAULT NULL,
  `broker_id` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`instance_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dpp_resources`.`processing_cluster_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`processing_cluster_info` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`processing_cluster_info` (
  `cluster_id` INT(11) NOT NULL,
  `no_of_nodes` INT(11) NULL DEFAULT NULL,
  `instance_types` VARCHAR(250) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `master_instance_id` VARCHAR(45) NULL DEFAULT NULL,
  `master_public_dnsname` VARCHAR(128) NULL DEFAULT NULL,
  `master_public_ip` VARCHAR(45) NULL DEFAULT NULL,
  `master_private_ip` VARCHAR(45) NULL DEFAULT NULL,
  `throughput` INT(11) NULL DEFAULT NULL,
  `latency` INT(11) NULL DEFAULT NULL,
  `batch_interval` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`cluster_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dpp_resources`.`processing_nodes_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`processing_nodes_info` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`processing_nodes_info` (
  `instance_id` VARCHAR(45) NOT NULL,
  `instance_type` VARCHAR(45) NULL DEFAULT NULL,
  `availability_zone` VARCHAR(45) NULL DEFAULT NULL,
  `public_dnsname` VARCHAR(128) NULL DEFAULT NULL,
  `public_ip` VARCHAR(45) NULL DEFAULT NULL,
  `private_ip` VARCHAR(45) NULL DEFAULT NULL,
  `status` VARCHAR(45) NULL DEFAULT NULL,
  `node_type` VARCHAR(45) NULL DEFAULT NULL,
  `alloc_type` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`instance_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dpp_resources`.`storage_cluster_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`storage_cluster_info` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`storage_cluster_info` (
  `cluster_id` INT(11) NOT NULL,
  `no_of_nodes` INT(11) NULL DEFAULT NULL,
  `instance_types` VARCHAR(45) NULL DEFAULT NULL,
  `throughput` INT(11) NULL DEFAULT NULL,
  `latency` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`cluster_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dpp_resources`.`storage_nodes_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`storage_nodes_info` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`storage_nodes_info` (
  `instance_id` VARCHAR(56) NOT NULL,
  `instance_type` VARCHAR(45) NOT NULL,
  `availability_zone` VARCHAR(45) NULL DEFAULT NULL,
  `public_dnsname` VARCHAR(128) NULL DEFAULT NULL,
  `public_ip` VARCHAR(45) NULL DEFAULT NULL,
  `private_ip` VARCHAR(45) NULL DEFAULT NULL,
  `status` VARCHAR(45) NULL DEFAULT NULL,
  `node_hostId` VARCHAR(56) NULL DEFAULT NULL,
  `node_type` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`instance_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dpp_resources`.`sustainable_qos_profile`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dpp_resources`.`sustainable_qos_profile` ;

CREATE TABLE IF NOT EXISTS `dpp_resources`.`sustainable_qos_profile` (
  `service_name` VARCHAR(45) NOT NULL,
  `instance_type` VARCHAR(45) NOT NULL,
  `input_qos_values` JSON NULL DEFAULT NULL,
  PRIMARY KEY (`service_name`, `instance_type`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

INSERT INTO `dpp_resources`.`ingestion_cluster_info` (`cluster_id`,`topic_name`,`data_ingestion_rate`) VALUES (100,'vehicle-data-stream',0);
INSERT INTO `dpp_resources`.`processing_cluster_info` (`cluster_id`,`batch_interval`) VALUES (100,1000);
INSERT INTO `dpp_resources`.`storage_cluster_info` (`cluster_id`) VALUES (100);
