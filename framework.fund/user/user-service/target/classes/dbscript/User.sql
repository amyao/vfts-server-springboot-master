-- --------------------------------------------------------
--
-- Table structure for table `UserEntity`
--
CREATE DATABASE IF NOT EXISTS db;

use db;

CREATE TABLE If Not Exists `USER_ENTITY` (
    `uuid` varchar(64) NOT NULL,
    `username` varchar(20) NOT NULL,
    `pwd` varchar(64) DEFAULT NULL,
    `payPwd` varchar(20) DEFAULT NULL,
    `questionIndex` int(20) DEFAULT NULL,
    `answer` varchar(64) DEFAULT NULL,
    primary key(uuid),
    unique key(uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
