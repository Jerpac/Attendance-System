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
-- Table `attendancesystem`.`course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`course` (
  `courseID` INT NOT NULL AUTO_INCREMENT,
  `courseName` VARCHAR(255) NULL DEFAULT NULL,
  `section` INT NOT NULL,
  PRIMARY KEY (`courseID`, `section`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`student`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`student` (
  `studentID` INT NOT NULL,
  `studentFName` VARCHAR(45) NULL DEFAULT NULL,
  `studentLName` VARCHAR(45) NULL DEFAULT NULL,
  `username` VARCHAR(45) NULL,
  `enrolledIn` INT NULL,
  PRIMARY KEY (`studentID`),
  INDEX `student_ibfk_1_idx` (`enrolledIn` ASC) VISIBLE,
  CONSTRAINT `student_ibfk_1`
    FOREIGN KEY (`enrolledIn`)
    REFERENCES `attendancesystem`.`section` (`sectionNum`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`section`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`section` (
  `sCourseID` INT NOT NULL,
  `sectionNum` INT NOT NULL,
  `secTime` INT NULL DEFAULT NULL,
  `professor` INT NULL DEFAULT NULL,
  `enrolledStudents` INT NULL,
  PRIMARY KEY (`sectionNum`, `sCourseID`),
  INDEX `sCourseID` (`sCourseID` ASC) VISIBLE,
  INDEX `professorID` (`professor` ASC) VISIBLE,
  INDEX `student-idfk_idx` (`enrolledStudents` ASC) VISIBLE,
  CONSTRAINT `section_ibfk_1`
    FOREIGN KEY (`sCourseID`)
    REFERENCES `attendancesystem`.`course` (`courseID`),
  CONSTRAINT `section_ibfk_2`
    FOREIGN KEY (`professor`)
    REFERENCES `attendancesystem`.`professor` (`professorID`),
  CONSTRAINT `student-idfk`
    FOREIGN KEY (`enrolledStudents`)
    REFERENCES `attendancesystem`.`student` (`studentID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`professor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`professor` (
  `professorID` INT NOT NULL,
  `professorName` VARCHAR(255) NULL DEFAULT NULL,
  `teaching` INT NULL,
  PRIMARY KEY (`professorID`),
  INDEX `teaching-sectionfk_idx` (`teaching` ASC) VISIBLE,
  CONSTRAINT `teaching-sectionfk`
    FOREIGN KEY (`teaching`)
    REFERENCES `attendancesystem`.`section` (`sectionNum`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`quizquestions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`quizquestions` (
  `questionID` INT NOT NULL AUTO_INCREMENT,
  `courseID` INT NULL DEFAULT NULL,
  `questionContent` TEXT NOT NULL,
  `answer1` TEXT NOT NULL,
  `answer2` TEXT NOT NULL,
  `answer3` TEXT NOT NULL,
  `answer4` TEXT NOT NULL,
  `correct_answer` ENUM('answer1', 'answer2', 'answer3', 'answer4') NOT NULL,
  `user_choice` ENUM('answer1', 'answer2', 'answer3', 'answer4') NULL DEFAULT NULL,
  `is_correct` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`questionID`),
  INDEX `courseID` (`courseID` ASC) VISIBLE,
  CONSTRAINT `quizquestions_ibfk_1`
    FOREIGN KEY (`courseID`)
    REFERENCES `attendancesystem`.`course` (`courseID`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `attendancesystem`.`studentresponses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendancesystem`.`studentresponses` (
  `response_id` INT NOT NULL AUTO_INCREMENT,
  `studentID` INT NOT NULL,
  `parentQuestionID` INT NOT NULL,
  `user_choice` ENUM('answer1', 'answer2', 'answer3', 'answer4') NULL DEFAULT NULL,
  `is_correct` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`response_id`),
  INDEX `studentID` (`studentID` ASC) VISIBLE,
  INDEX `parentQuestionID` (`parentQuestionID` ASC) VISIBLE,
  CONSTRAINT `studentresponses_ibfk_1`
    FOREIGN KEY (`studentID`)
    REFERENCES `attendancesystem`.`student` (`studentID`),
  CONSTRAINT `studentresponses_ibfk_2`
    FOREIGN KEY (`parentQuestionID`)
    REFERENCES `attendancesystem`.`quizquestions` (`questionID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Insert new questions like this
INSERT INTO QuizQuestions (questionContent, answer1, answer2, answer3, answer4, correct_answer)
VALUES ('What is the capital of France?', 'Berlin', 'Paris', 'Rome', 'Madrid', 'answer2');

-- Update user's choice and check if it is correct
UPDATE QuizQuestions
SET user_choice = 'answer2', is_correct = (correct_answer = 'answer2')
WHERE questionID = 1;
-- In this case since the user's choice is 'answer2' (Paris), and is_correct will be set to TRUE if 'answer2' is the correct answer, otherwise FALSE

INSERT INTO student (studentID, studentFName, studentLName)
VALUES ('12345', 'Anakin', 'Ha');

DELETE FROM student WHERE studentID = '12345';

