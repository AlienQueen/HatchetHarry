-- MySQL dump 10.13  Distrib 5.5.40, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: hh
-- ------------------------------------------------------
-- Server version	5.5.40-0ubuntu1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `hh`
--

USE `hh`;

--
-- Table structure for table `Arrow`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Arrow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gameId` bigint(20) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `target` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `UK_6vchq7txxyjip8j0mxdkyogtp` (`gameId`),
  KEY `UK_i1on1xqo2i7p9256k3ta28392` (`source`),
  KEY `UK_q1wxw123kpwccpe53vir1x43l` (`target`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CardCollection`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CardCollection` (
  `id` varchar(255) NOT NULL,
  `altart` varchar(255) DEFAULT NULL,
  `artist` varchar(255) DEFAULT NULL,
  `cardnum` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `cost` varchar(255) DEFAULT NULL,
  `expansionSet` varchar(1024) DEFAULT NULL,
  `flavor` varchar(255) DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `power` varchar(255) DEFAULT NULL,
  `printedname` varchar(255) DEFAULT NULL,
  `printedrules` varchar(255) DEFAULT NULL,
  `printedtype` varchar(255) DEFAULT NULL,
  `rarity` varchar(255) DEFAULT NULL,
  `rules` varchar(1024) DEFAULT NULL,
  `rulings` varchar(255) DEFAULT NULL,
  `sets` varchar(6000) DEFAULT NULL,
  `toughness` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  `watermark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Card_Counter`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Card_Counter` (
  `uuid` bigint(20) NOT NULL,
  `counterId` bigint(20) NOT NULL,
  PRIMARY KEY (`uuid`,`counterId`),
  UNIQUE KEY `UK_r3t7cryxpmro68uqysxvaop93` (`counterId`),
  CONSTRAINT `FK_5l5ryfdjyy3vnwcduib0l2o6` FOREIGN KEY (`uuid`) REFERENCES `Token` (`tokenId`),
  CONSTRAINT `FK_r3t7cryxpmro68uqysxvaop93` FOREIGN KEY (`counterId`) REFERENCES `Counter` (`counterId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ChatMessage`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ChatMessage` (
  `id` bigint(20) NOT NULL,
  `gameId` bigint(20) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `UK_s6nh5pnd88l7yfjopkx4wtqlt` (`gameId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CollectibleCard`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CollectibleCard` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deckArchiveId` bigint(20) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `UK_cm0y2bec27v0q1fvk6yxeli89` (`title`),
  KEY `UK_bhnbve3p12cr7ly46q55e3so4` (`deckArchiveId`)
) ENGINE=InnoDB AUTO_INCREMENT=181 DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ConsoleLogMessage`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ConsoleLogMessage` (
  `id` bigint(20) NOT NULL,
  `gameId` bigint(20) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7kgaej3u1wssnx829hm3pj3fu` (`gameId`,`message`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Counter`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Counter` (
  `counterId` bigint(20) NOT NULL AUTO_INCREMENT,
  `counterName` varchar(255) DEFAULT NULL,
  `numberOfCounters` bigint(20) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  `card` bigint(20) DEFAULT NULL,
  `token` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`counterId`),
  KEY `UK_mi5fkgeit0qlh0b732uqmub18` (`card`),
  KEY `UK_jvrqub2we680h66ldbftgexsc` (`token`),
  CONSTRAINT `FK_jvrqub2we680h66ldbftgexsc` FOREIGN KEY (`token`) REFERENCES `Token` (`tokenId`),
  CONSTRAINT `FK_mi5fkgeit0qlh0b732uqmub18` FOREIGN KEY (`card`) REFERENCES `MagicCard` (`magicCardId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Deck`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Deck` (
  `deckId` bigint(20) NOT NULL AUTO_INCREMENT,
  `playerId` bigint(20) DEFAULT NULL,
  `Deck_DeckArchive` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`deckId`),
  KEY `UK_apfhe8d47k7buoittvg26vnt0` (`Deck_DeckArchive`),
  KEY `UK_a5rdbfvf61h9ibr5j9176his2` (`playerId`),
  CONSTRAINT `FK_apfhe8d47k7buoittvg26vnt0` FOREIGN KEY (`Deck_DeckArchive`) REFERENCES `DeckArchive` (`deckArchiveId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DeckArchive`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DeckArchive` (
  `deckArchiveId` bigint(20) NOT NULL AUTO_INCREMENT,
  `deckName` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`deckArchiveId`),
  KEY `UK_c0lwjejpu2cjl6qfskktxpwjd` (`deckName`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Game`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Game` (
  `gameId` bigint(20) NOT NULL AUTO_INCREMENT,
  `currentPlaceholderId` bigint(20) DEFAULT NULL,
  `desiredFormat` int(11) DEFAULT NULL,
  `desiredNumberOfPlayers` int(11) DEFAULT NULL,
  `isDrawMode` bit(1) DEFAULT NULL,
  `pending` bit(1) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`gameId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MagicCard`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MagicCard` (
  `magicCardId` bigint(20) NOT NULL AUTO_INCREMENT,
  `battlefieldOrder` int(11) DEFAULT NULL,
  `bigImageFilename` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `gameId` bigint(20) DEFAULT NULL,
  `ownerSide` varchar(255) DEFAULT NULL,
  `smallImageFilename` varchar(255) DEFAULT NULL,
  `tapped` bit(1) DEFAULT NULL,
  `thumbnailFilename` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  `x` bigint(20) DEFAULT NULL,
  `y` bigint(20) DEFAULT NULL,
  `zone` varchar(255) DEFAULT NULL,
  `zoneOrder` bigint(20) DEFAULT NULL,
  `card_deck` bigint(20) DEFAULT NULL,
  `token_tokenId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`magicCardId`),
  KEY `UK_ri4k7ri038rk9l4yf63cglfps` (`uuid`),
  KEY `UK_kjrahfndtgsfk5ysn6t9re76q` (`gameId`),
  KEY `UK_dmaon4s576aqspe2fksjwmnih` (`card_deck`),
  KEY `FK_4hb7814gg4osu3r3bb2y3tepb` (`token_tokenId`),
  CONSTRAINT `FK_4hb7814gg4osu3r3bb2y3tepb` FOREIGN KEY (`token_tokenId`) REFERENCES `Token` (`tokenId`),
  CONSTRAINT `FK_dmaon4s576aqspe2fksjwmnih` FOREIGN KEY (`card_deck`) REFERENCES `Deck` (`deckId`)
) ENGINE=InnoDB AUTO_INCREMENT=181 DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ParentMessage`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ParentMessage` (
  `id` bigint(20) NOT NULL,
  `gameId` bigint(20) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Player`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Player` (
  `playerId` bigint(20) NOT NULL AUTO_INCREMENT,
  `defaultTargetZoneForExile` int(11) DEFAULT NULL,
  `defaultTargetZoneForGraveyard` int(11) DEFAULT NULL,
  `defaultTargetZoneForHand` int(11) DEFAULT NULL,
  `isExileDisplayed` bit(1) DEFAULT NULL,
  `isGraveyardDisplayed` bit(1) DEFAULT NULL,
  `isHandDisplayed` bit(1) DEFAULT NULL,
  `lifePoints` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sideUuid` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  `deck` bigint(20) DEFAULT NULL,
  `game_gameId` bigint(20) DEFAULT NULL,
  `Player_Side` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`playerId`),
  KEY `UK_57uf61wcjmwjr2q3y15ucs2jg` (`Player_Side`),
  KEY `UK_lc5jhddp25d3ytjwb2977abwa` (`deck`),
  KEY `FK_sww11fr07rgwqneeu2bu7xti8` (`game_gameId`),
  CONSTRAINT `FK_57uf61wcjmwjr2q3y15ucs2jg` FOREIGN KEY (`Player_Side`) REFERENCES `Side` (`sideId`),
  CONSTRAINT `FK_lc5jhddp25d3ytjwb2977abwa` FOREIGN KEY (`deck`) REFERENCES `Deck` (`deckId`),
  CONSTRAINT `FK_sww11fr07rgwqneeu2bu7xti8` FOREIGN KEY (`game_gameId`) REFERENCES `Game` (`gameId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Player_Game`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Player_Game` (
  `gameId` bigint(20) NOT NULL,
  `playerId` bigint(20) NOT NULL,
  PRIMARY KEY (`gameId`,`playerId`),
  UNIQUE KEY `UK_28we0l42gosnc7xpfwrl8dhwf` (`playerId`),
  CONSTRAINT `FK_3c9s7ggv40klkg7da1jy3ved0` FOREIGN KEY (`gameId`) REFERENCES `Game` (`gameId`),
  CONSTRAINT `FK_28we0l42gosnc7xpfwrl8dhwf` FOREIGN KEY (`playerId`) REFERENCES `Player` (`playerId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Side`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Side` (
  `sideId` bigint(20) NOT NULL AUTO_INCREMENT,
  `sideName` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  `wicketId` varchar(255) DEFAULT NULL,
  `x` bigint(20) DEFAULT NULL,
  `y` bigint(20) DEFAULT NULL,
  `game_gameId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`sideId`),
  KEY `UK_qnwlixf7ubochjqeee9rfuh4p` (`uuid`),
  KEY `FK_pcsyw1d64ohtki1hcoxs3ci43` (`game_gameId`),
  CONSTRAINT `FK_pcsyw1d64ohtki1hcoxs3ci43` FOREIGN KEY (`game_gameId`) REFERENCES `Game` (`gameId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Token`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Token` (
  `tokenId` bigint(20) NOT NULL AUTO_INCREMENT,
  `capabilities` varchar(255) DEFAULT NULL,
  `colors` varchar(255) DEFAULT NULL,
  `creatureTypes` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `gameId` bigint(20) DEFAULT NULL,
  `power` varchar(255) DEFAULT NULL,
  `tapped` bit(1) DEFAULT NULL,
  `toughness` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  `x` bigint(20) DEFAULT NULL,
  `y` bigint(20) DEFAULT NULL,
  `Player_Token` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`tokenId`),
  KEY `UK_agwe887k790jsyve1pnpwk6b` (`uuid`),
  KEY `UK_b6ol3o1ettcs6fkn6pkpriexc` (`Player_Token`),
  CONSTRAINT `FK_b6ol3o1ettcs6fkn6pkpriexc` FOREIGN KEY (`Player_Token`) REFERENCES `Player` (`playerId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `User` (
  `username` varchar(255) NOT NULL,
  `identity` varchar(255) DEFAULT NULL,
  `isFacebook` bit(1) DEFAULT NULL,
  `login` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `privateIdentity` varchar(255) DEFAULT NULL,
  `realm` varchar(255) DEFAULT NULL,
  `VERSION` varchar(20) DEFAULT NULL,
  `player_playerId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`username`),
  KEY `FK_rlprh0ciixdxluvkh36nysa80` (`player_playerId`),
  CONSTRAINT `FK_rlprh0ciixdxluvkh36nysa80` FOREIGN KEY (`player_playerId`) REFERENCES `Player` (`playerId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequences`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequences` (
  `sequence_name` varchar(255) DEFAULT NULL,
  `sequence_next_hi_value` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-01-22 11:30:22
