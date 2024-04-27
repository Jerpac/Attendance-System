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
-- Table `attendancesystem`.`student`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`student` (
  `studentID` INT NOT NULL,
  `studentFName` VARCHAR(45) NULL DEFAULT NULL,
  `studentLName` VARCHAR(45) NULL DEFAULT NULL,
  `username` VARCHAR(45) NULL DEFAULT NULL,
  `enrolledIn` INT NULL DEFAULT NULL,
  `present` TINYINT(1) NULL,
  `attendance-rate` DECIMAL(5,2) NULL,
  PRIMARY KEY (`studentID`),
  INDEX `student_ibfk_1_idx` (`enrolledIn` ASC) VISIBLE,
  CONSTRAINT `student-class-idfk`
    FOREIGN KEY (`enrolledIn`)
    REFERENCES `attendancesystem`.`class` (`class-id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`class`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`class` (
  `class-id` INT NOT NULL,
  `course-sec` VARCHAR(10) NULL,
  `time` DATETIME NULL DEFAULT NULL,
  `professor-id` INT NULL DEFAULT NULL,
  `enrolledStudents` INT NULL DEFAULT NULL,
  PRIMARY KEY (`class-id`),
  INDEX `professorID` (`professor-id` ASC) VISIBLE,
  INDEX `class-student-idfk_idx` (`enrolledStudents` ASC) VISIBLE,
  CONSTRAINT `class-prof-idfk`
    FOREIGN KEY (`professor-id`)
    REFERENCES `attendancesystem`.`professor` (`professorID`),
  CONSTRAINT `class-student-idfk`
    FOREIGN KEY (`enrolledStudents`)
    REFERENCES `attendancesystem`.`student` (`studentID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`professor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`professor` (
  `professorID` INT NOT NULL,
  `professorName` VARCHAR(255) NULL DEFAULT NULL,
  `teaching` INT NULL DEFAULT NULL,
  PRIMARY KEY (`professorID`),
  INDEX `teaching-sectionfk_idx` (`teaching` ASC) VISIBLE,
  CONSTRAINT `teaching-sectionfk`
    FOREIGN KEY (`teaching`)
    REFERENCES `attendancesystem`.`class` (`course-sec`))
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
  `quizPassword` VARCHAR(255) NULL DEFAULT NULL,
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
-- Table `attendancesystem`.`studentresponses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`studentresponses` (
  `response_id` INT NOT NULL AUTO_INCREMENT,
  `studentID` INT NOT NULL,
  `questionID` INT NOT NULL,
  `user_choice` ENUM('answer1', 'answer2', 'answer3', 'answer4') NULL DEFAULT NULL,
  `is_correct` TINYINT NULL DEFAULT NULL,
  PRIMARY KEY (`response_id`),
  INDEX `studentID` (`studentID` ASC) VISIBLE,
  INDEX `parentQuestionID` (`questionID` ASC) VISIBLE,
  CONSTRAINT `responses-studentid-fk`
    FOREIGN KEY (`studentID`)
    REFERENCES `attendancesystem`.`student` (`studentID`),
  CONSTRAINT `responses-parentquestionid-fk`
    FOREIGN KEY (`questionID`)
    REFERENCES `attendancesystem`.`quizquestions` (`questionID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
