-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema attendancesystem
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema attendancesystem
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `attendancesystem` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `attendancesystem` ;

-- -----------------------------------------------------
-- Table `attendancesystem`.`class`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`class` (
  `class-id` INT NOT NULL,
  `course-sec` VARCHAR(15) NULL DEFAULT 'CS-4000.000',
  `time` DATETIME NULL DEFAULT NULL,
  `quizPassword` INT NULL,
  `quiz_is_open` TINYINT NULL,
  PRIMARY KEY (`class-id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`quizquestions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`quizquestions` (
  `questionID` INT NOT NULL AUTO_INCREMENT,
  `classID` INT NULL DEFAULT NULL,
  `questionContent` TEXT NULL DEFAULT NULL,
  `answer1` TEXT NOT NULL,
  `answer2` TEXT NOT NULL,
  `answer3` TEXT NOT NULL,
  `answer4` TEXT NOT NULL,
  `correct_answer` ENUM('answer1', 'answer2', 'answer3', 'answer4') NOT NULL,
  `user_choice` ENUM('answer1', 'answer2', 'answer3', 'answer4') NULL DEFAULT NULL,
  `is_correct` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`questionID`),
  INDEX `quizquestions-class-idfk_idx` (`classID` ASC) VISIBLE,
  CONSTRAINT `quizquestions-class-idfk`
    FOREIGN KEY (`classID`)
    REFERENCES `attendancesystem`.`class` (`class-id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`student`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`student` (
  `studentID` INT NOT NULL,
  `studentFName` VARCHAR(45) NULL DEFAULT NULL,
  `studentLName` VARCHAR(45) NULL DEFAULT NULL,
  `username` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`studentID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`studentresponses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`studentresponses` (
  `response_id` INT NOT NULL AUTO_INCREMENT,
  `studentID` INT NOT NULL,
  `questionID` INT NOT NULL,
  `user_choice` ENUM('answer1', 'answer2', 'answer3', 'answer4') NULL DEFAULT NULL,
  `is_correct` TINYINT NULL DEFAULT NULL,
  INDEX `studentID` (`studentID` ASC) VISIBLE,
  INDEX `parentQuestionID` (`questionID` ASC) VISIBLE,
  PRIMARY KEY (`response_id`, `studentID`, `questionID`),
  CONSTRAINT `responses-studentid-fk`
    FOREIGN KEY (`studentID`)
    REFERENCES `attendancesystem`.`student` (`studentID`),
  CONSTRAINT `responses-parentquestionid-fk`
    FOREIGN KEY (`questionID`)
    REFERENCES `attendancesystem`.`quizquestions` (`questionID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`student_in_classes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`student_in_classes` (
  `studentID` INT NOT NULL,
  `classID` INT NOT NULL,
  `timesPresent` INT NULL,
  `timesAbsent` INT NULL,
  PRIMARY KEY (`studentID`, `classID`),
  INDEX `studentclasses-classid-fk_idx` (`classID` ASC) VISIBLE,
  CONSTRAINT `studentclasses-studid-fk`
    FOREIGN KEY (`studentID`)
    REFERENCES `attendancesystem`.`student` (`studentID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `studentclasses-classid-fk`
    FOREIGN KEY (`classID`)
    REFERENCES `attendancesystem`.`class` (`class-id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
