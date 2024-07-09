-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: mysql-rakcha.alwaysdata.net
-- Generation Time: Jun 09, 2024 at 09:21 PM
-- Server version: 10.6.16-MariaDB
-- PHP Version: 7.4.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `rakcha_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `actor`
--

CREATE TABLE `actor` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `image` longtext NOT NULL,
  `biographie` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `actor`
--

INSERT INTO `actor` (`id`, `nom`, `image`, `biographie`) VALUES
(1, 'RyuKyungsoo', '/img/actors/30425.jpg', 'Traduit de l\'anglais-Ryu Kyung-soo est un acteur sud-coréen. Il est connu pour ses rôles dans les séries dramatiques Confession et Itaewon Class'),
(2, 'Tom Holland', '/img/actors/97434.jpg', 'Thomas Stanley Holland, dit Tom Holland, né le 1ᵉʳ juin 1996 à Kingston upon Thames'),
(3, 'Zendaya', '/img/actors/3033.jpg', 'Zendaya Coleman, dite simplement Zendaya, est une actrice, productrice, mannequin,'),
(4, 'Tobey Maguire', '/img/actors/40317.jpg', 'Tobey Maguire, de son vrai nom Tobias Vincent Maguire, né le 27 juin 1975 à Santa Monica'),
(5, 'Robert Downey Jr', '/img/actors/15329.jpg', 'Robert Downey Jr. /ˈɹɑbɚt ˈdaʊni ˈd͡ʒunjɚ/, né le 4 avril 1965 à New York, est un acteur et scénariste américain.'),
(6, 'Chris Hemsworth', '/img/actors/30524.jpg', 'Christopher Hemsworth, dit Chris, né le 11 août 1983 à Melbourne, est un acteur australien.'),
(7, 'Chris Evans', '/img/actors/43656.jpg', 'Christopher Robert Evans est un acteur américain, né le 13 juin 1981 à Boston'),
(8, 'Cillian Murphy', '/img/actors/85261.jpg', 'Cillian Murphy, né le 25 mai 1976 à Douglas, est un acteur et musicien irlandais.'),
(9, 'Tom Hardy', '/img/actors/23056.jpg', 'Edward Hardy, dit Tom Hardy, né le 15 septembre 1977 à Hammersmith en Angleterre, est un acteur et producteur britannique');

-- --------------------------------------------------------

--
-- Table structure for table `actorfilm`
--

CREATE TABLE `actorfilm` (
  `idactor` int(11) NOT NULL,
  `idfilm` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `avis`
--

CREATE TABLE `avis` (
  `id` int(11) NOT NULL,
  `idusers` int(11) DEFAULT NULL,
  `id_produit` int(11) DEFAULT NULL,
  `note` int(11) NOT NULL,
  `avis` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `idcategorie` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `description` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`idcategorie`, `nom`, `description`) VALUES
(1, 'Drama', 'serie drama'),
(2, 'Horreur', 'serie horreur'),
(3, 'Fantasy', 'serie fantasy'),
(4, 'Comedy', 'serie comedy');

-- --------------------------------------------------------

--
-- Table structure for table `categorie_evenement`
--

CREATE TABLE `categorie_evenement` (
  `ID` int(11) NOT NULL,
  `Nom_categorie` varchar(50) NOT NULL,
  `Description` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categorie_evenement`
--

INSERT INTO `categorie_evenement` (`ID`, `Nom_categorie`, `Description`) VALUES
(1, 'MovieProjection', 'Projections of trendy and popular movies'),
(2, 'MovieDebate', 'Debates of popular and trendy movies'),
(3, 'Party', 'We do parties here !');

-- --------------------------------------------------------

--
-- Table structure for table `categorie_produit`
--

CREATE TABLE `categorie_produit` (
  `id_categorie` int(11) NOT NULL,
  `nom_categorie` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categorie_produit`
--

INSERT INTO `categorie_produit` (`id_categorie`, `nom_categorie`, `description`) VALUES
(2, 'POSTER', 'Transform your walls into a movie buff\'s dream gallery with our unique cinematic paintings'),
(3, 'Sculptures', 'Sculptures are three-dimensional works of art to represent shapes, figures or artistic objects.'),
(4, 'Books', 'Immerse yourself in the magical world of cinema with our collection of captivating books'),
(8, 'Tableau', 'tabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb');

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `description` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `nom`, `description`) VALUES
(1, 'Action', 'Des films remplis d\'aventures, de combats, de cascades et d\'exploits héroïques. Ils mettent souvent en avant des personnages principaux confrontés à des défis physiques ou à des situations dangereuses.'),
(2, 'Adventure', 'Movies that involve exciting journeys, exploration of new territories, and often include elements of danger, discovery, and heroism.'),
(3, 'Comedy', ' Films designed to entertain and amuse through humor, wit, and exaggerated situations, often aiming to provoke laughter from the audience.'),
(4, 'Drama', 'Movies focused on realistic characters and their emotional struggles, often exploring themes such as love, loss, conflict, and personal growth.'),
(5, 'Horror', 'Films intended to evoke fear, disgust, or shock in the audience through suspenseful storytelling, supernatural elements, or gruesome imagery.'),
(6, 'Science Fiction', 'Movies set in speculative futures or alternative realities, often featuring advanced technology, space exploration, and imaginative concepts about the universe.'),
(7, 'Fantasy', 'Films that incorporate magical or supernatural elements, mythical creatures, and imaginative worlds, often drawing inspiration from folklore, mythology, or fantasy literature.'),
(8, 'Thriller', 'Movies characterized by suspenseful and tense narratives, often involving crime, espionage, or psychological gamesmanship, aimed at keeping the audience on the edge of their seats.'),
(9, 'Mystery', 'Films that revolve around solving a puzzle or uncovering secrets, typically involving a detective or amateur sleuth as the central character.'),
(10, 'Romance', 'Movies centered on love stories and romantic relationships, often featuring themes of passion, longing, and emotional connection between characters.'),
(11, 'Animation', 'Films created using animation techniques, including traditional hand-drawn animation, computer-generated imagery (CGI), or stop-motion animation, appealing to audiences of all ages.'),
(12, 'Musical', 'Movies that incorporate song and dance sequences as integral parts of the narrative, often featuring characters expressing their emotions or advancing the plot through music.'),
(13, 'Documentary', 'Non-fiction films that aim to educate, inform, or raise awareness about real-life subjects, events, or issues, often employing interviews, archival footage, and voice-over narration.'),
(14, 'Biography', 'Films based on the lives of real people, exploring their achievements, struggles, and significant moments, often offering insights into their personal and professional journeys.'),
(15, 'Historical', 'Movies set in the past, depicting historical events, figures, or periods, often blending factual accuracy with dramatic storytelling to recreate historical moments.');

-- --------------------------------------------------------

--
-- Table structure for table `cinema`
--

CREATE TABLE `cinema` (
  `id_cinema` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `adresse` varchar(100) NOT NULL,
  `responsable` int(11) NOT NULL,
  `logo` varchar(1000) NOT NULL,
  `Statut` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cinema`
--

INSERT INTO `cinema` (`id_cinema`, `nom`, `adresse`, `responsable`, `logo`, `Statut`) VALUES
(13, 'Cinema l\'étoile', 'Tunis', 104, 'img/cinemas/etoile.png-6634a60d82254.png', 'Accepted'),
(14, 'cinemaAli', 'Tunis', 104, 'img/cinemas/pathe.png-6634a9490827b.png', 'Accepted'),
(15, 'veo', 'Tunis', 104, 'img/cinemas/veo_cinema.png-66384172ce206.png', 'Accepted'),
(16, 'L\'AGORA', 'Sousse', 104, 'img/cinemas/agora-logo.png-6638419a730b9.png', 'Pending'),
(18, 'ABC Cinema', 'Monastir', 104, 'img/cinemas/5db23739-d572-4b26-9031-996a1e6b49a4.jpeg-663841e1de710.jpg', 'Pending'),
(19, 'palace', 'monastir, rue habib bourguiba', 104, 'img/cinemas/palace.jpeg-6638427a8c005.jpg', 'Pending'),
(20, 'Coliser', 'Tunis', 104, 'img/cinemas/coliser.jpeg-6638429673906.jpg', 'Pending'),
(21, 'pathé sousse', 'mall of sousse', 118, 'img/cinemas/path.png-663877fde00c5.png', 'Accepted'),
(22, 'Lagora', 'monastir, rue habib bourguiba', 118, 'img/cinemas/agora-logo.png-66388b4f90f8e.png', 'Pending'),
(23, 'ABC Cinema', 'Tunis', 118, 'img/cinemas/abc.jpeg-66388b7ecd789.jpg', 'Pending'),
(25, 'VEO', 'monastir, rue habib bourguiba', 118, 'img/cinemas/veo_cinema.png-66388c3bb00d5.png', 'Accepted');

-- --------------------------------------------------------

--
-- Table structure for table `commande`
--

CREATE TABLE `commande` (
  `idCommande` int(11) NOT NULL,
  `dateCommande` date NOT NULL,
  `statu` varchar(50) NOT NULL DEFAULT 'En cours',
  `num_telephone` int(11) NOT NULL,
  `adresse` varchar(50) NOT NULL,
  `idClient` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `commande`
--

INSERT INTO `commande` (`idCommande`, `dateCommande`, `statu`, `num_telephone`, `adresse`, `idClient`) VALUES
(52, '2024-05-06', 'en cours', 29201398, 'Bennane', 113),
(53, '2024-05-06', 'en cours', 29201398, 'Bennane', 113),
(54, '2024-05-06', 'en cours', 94636469, 'tunis', 113),
(55, '2024-05-06', 'en cours', 94636469, 'tunis', 113),
(56, '2024-05-06', 'en cours', 94636469, 'tunis', 113);

-- --------------------------------------------------------

--
-- Table structure for table `commandeitem`
--

CREATE TABLE `commandeitem` (
  `id_produit` int(11) DEFAULT NULL,
  `idCommandeItem` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `idCommande` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `commandeitem`
--

INSERT INTO `commandeitem` (`id_produit`, `idCommandeItem`, `quantity`, `idCommande`) VALUES
(1, 20, 5, 52),
(1, 21, 5, 53),
(19, 22, 2, 54);

-- --------------------------------------------------------

--
-- Table structure for table `commentaire`
--

CREATE TABLE `commentaire` (
  `idcommentaire` int(11) NOT NULL,
  `commentaire` varchar(1000) NOT NULL,
  `idClient` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `commentairecinema`
--

CREATE TABLE `commentairecinema` (
  `id` int(11) NOT NULL,
  `idclient` int(11) NOT NULL,
  `idcinema` int(11) NOT NULL,
  `commentaire` varchar(5000) NOT NULL,
  `sentiment` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `commentairecinema`
--

INSERT INTO `commentairecinema` (`id`, `idclient`, `idcinema`, `commentaire`, `sentiment`) VALUES
(1, 104, 10, 'good cinema', 'pos'),
(2, 104, 10, 'bad cinema', 'neg'),
(3, 104, 11, 'good cinema', 'pos'),
(4, 113, 13, 'good cinema', 'pos'),
(5, 113, 13, 'bad cinema', 'neg'),
(6, 113, 13, 'great', 'pos'),
(7, 113, 13, 'belle', 'neu');

-- --------------------------------------------------------

--
-- Table structure for table `commentaire_produit`
--

CREATE TABLE `commentaire_produit` (
  `id` int(11) NOT NULL,
  `id_client_id` int(11) DEFAULT NULL,
  `id_produit` int(11) DEFAULT NULL,
  `commentaire` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `commentaire_produit`
--

INSERT INTO `commentaire_produit` (`id`, `id_client_id`, `id_produit`, `commentaire`) VALUES
(2, 113, 1, 'Good product'),
(3, 113, 1, 'Love');

-- --------------------------------------------------------

--
-- Table structure for table `doctrine_migration_versions`
--

CREATE TABLE `doctrine_migration_versions` (
  `version` varchar(191) NOT NULL,
  `executed_at` datetime DEFAULT NULL,
  `execution_time` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `doctrine_migration_versions`
--

INSERT INTO `doctrine_migration_versions` (`version`, `executed_at`, `execution_time`) VALUES
('DoctrineMigrations\\Version20240501104445', '2024-05-01 12:45:51', 270),
('DoctrineMigrations\\Version20240501104736', '2024-05-01 12:49:07', 9),
('DoctrineMigrations\\Version20240501104806', '2024-05-01 12:49:24', 11),
('DoctrineMigrations\\Version20240502191446', '2024-05-06 05:17:33', 350);

-- --------------------------------------------------------

--
-- Table structure for table `episodes`
--

CREATE TABLE `episodes` (
  `idepisode` int(11) NOT NULL,
  `idserie` int(11) DEFAULT NULL,
  `titre` varchar(30) NOT NULL,
  `numeroepisode` int(11) NOT NULL,
  `saison` int(11) NOT NULL,
  `image` varchar(255) NOT NULL,
  `video` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `episodes`
--

INSERT INTO `episodes` (`idepisode`, `idserie`, `titre`, `numeroepisode`, `saison`, `image`, `video`) VALUES
(4, 13, 'Fake friends', 1, 2, 'img/series/aaaabd0brbfrfhksln3x8p89bmxakxbjk65vki3gnqui4urylpurc6fzyf4wxhvx4jfsytmdhxmz_mkofj9pkubgjf0abk5eouj8uq4vwt6f3v3suqt7o6bc2n4p-6637f85119072.jpg', 'img/series/3413033377-preview-6637f85119b42.mp4'),
(5, 13, 'Masculine Feminine', 2, 1, 'img/series/aaaabrfb3dy2tyofrz8blnqrghfouufgaptipyaoonhlmecrp_vawzjo5i_zukdm-ks2h-du2giwgnywilpe-hhdert-6weu55q2wrk3xz07ubtw5u_csozhqu59-6637f8bba3e68.jpg', 'img/series/3413033377-preview-6637f8bba4a7b.mp4');

-- --------------------------------------------------------

--
-- Table structure for table `evenement`
--

CREATE TABLE `evenement` (
  `id_categorie` int(11) DEFAULT NULL,
  `ID` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `dateDebut` date NOT NULL,
  `dateFin` date NOT NULL,
  `lieu` varchar(50) NOT NULL,
  `etat` varchar(50) NOT NULL,
  `description` varchar(500) NOT NULL,
  `affiche_event` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `evenement`
--

INSERT INTO `evenement` (`id_categorie`, `ID`, `nom`, `dateDebut`, `dateFin`, `lieu`, `etat`, `description`, `affiche_event`) VALUES
(1, 1, 'SuperManProjection', '2019-01-01', '2019-01-06', 'Esprit', 'Incoming', 'JoinUsToWatchSpiderManInEsprit', 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3Dz'),
(1, 2, 'HollywoodMovie', '2024-05-01', '2024-05-02', 'Esprit', 'Incoming', 'ThisIsAMovieProjection !', 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
(2, 3, 'LetsDebate', '2024-05-01', '2024-05-03', 'Esprit', 'Ongoing', 'This is a movie debate !', 'https://images.unsplash.com/photo-1544531586-fde5298cdd40?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
(3, 4, 'PartyNow', '2021-05-01', '2021-05-03', 'Esprit', 'Incoming', 'this is a party', 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3Dz');

-- --------------------------------------------------------

--
-- Table structure for table `favoris`
--

CREATE TABLE `favoris` (
  `id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `id_serie` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `favoris`
--

INSERT INTO `favoris` (`id`, `id_user`, `id_serie`) VALUES
(1, 1, 1),
(2, 1, 2),
(6, 108, 2);

-- --------------------------------------------------------

--
-- Table structure for table `feedback`
--

CREATE TABLE `feedback` (
  `id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `description` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `id_episode` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `feedback`
--

INSERT INTO `feedback` (`id`, `id_user`, `description`, `date`, `id_episode`) VALUES
(1, 1, 'ahla nourhene', '2019-01-01', 2),
(2, 108, 'j\'ai aimé', '2019-01-01', 4),
(3, 108, 'j\'ai pas aimé', '2019-01-01', 4),
(4, 108, 'Excellent !!', '2019-01-01', 4),
(5, 108, 'j\'adore', '2019-01-01', 4);

-- --------------------------------------------------------

--
-- Table structure for table `feedback_evenement`
--

CREATE TABLE `feedback_evenement` (
  `id_user` int(11) DEFAULT NULL,
  `id_evenement` int(11) DEFAULT NULL,
  `ID` int(11) NOT NULL,
  `commentaire` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `film`
--

CREATE TABLE `film` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `image` longtext DEFAULT NULL,
  `duree` time NOT NULL,
  `description` longtext NOT NULL,
  `annederalisation` int(11) NOT NULL,
  `isBookmarked` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `film`
--

INSERT INTO `film` (`id`, `nom`, `image`, `duree`, `description`, `annederalisation`, `isBookmarked`) VALUES
(6, 'Spider', '/img/films/90101.jpg', '02:10:45', 'description ddd', 2008, 0),
(7, 'SpiderMan No Way Home', '/img/films/61459.jpg', '02:28:30', 'Avec l\'identité de Spiderman désormais révélée, celui-ci est démasqué', 2021, 1),
(8, 'Avengers  Infinity War', '/img/films/6794.jpg', '02:29:41', 'Alors que les Avengers et leurs alliés ont continué de protéger le monde face à des menaces bien trop grande', 2018, 1),
(9, 'Oppenheimer', '/img/films/46637.jpg', '03:00:48', 'En 1942, convaincus que l\'Allemagne nazie est en train de développer une arme nucléaire', 2024, 1),
(10, 'Venom Let There Be Carnage', '/img/films/62047.jpg', '01:37:42', 'Après leur triomphe sur Riot, Eddie Brock et son parasite extraterrestre se sont mis d\'accord sur quelques règles de conduite', 2021, 0),
(11, 'LIdee detre avec toi', '/img/films/79595.jpg', '01:55:36', 'Lorsque Solène, une mère célibataire de 40 ans, doit chaperonner le voyage de sa fille adolescente au festival de musique de Coachella,', 2024, 0),
(12, 'Dachra', '/img/films/39862.jpg', '01:32:56', 'Dachra est un film d\'horreur tunisien écrit et réalisé par Abdelhamid Bouchnak et sorti en 2018', 2018, 0),
(13, 'Titanic', '/img/films/43943.jpg', '03:23:40', 'Southampton, 10 avril 1912. Le paquebot le plus grand et le plus moderne du monde, réputé pour son insubmersibilité, le « Titanic »', 2008, 0),
(14, 'En eaux tres troubles', '/img/films/82547.jpg', '01:58:15', 'Une équipe de chercheurs en pleine opération d\'extraction minière illégale dans l\'océan se retrouve en péril.', 2023, 0);

-- --------------------------------------------------------

--
-- Table structure for table `filmcategory`
--

CREATE TABLE `filmcategory` (
  `film_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `filmcoment`
--

CREATE TABLE `filmcoment` (
  `id` int(11) NOT NULL,
  `comment` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL,
  `film_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `film_actor`
--

CREATE TABLE `film_actor` (
  `film_id` int(11) NOT NULL,
  `actor_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `film_actor`
--

INSERT INTO `film_actor` (`film_id`, `actor_id`) VALUES
(6, 1),
(7, 2),
(7, 3),
(7, 4),
(8, 5),
(8, 6),
(8, 7),
(9, 5),
(9, 8),
(10, 2),
(10, 9),
(11, 9),
(12, 7),
(13, 6),
(13, 7),
(14, 1),
(14, 6);

-- --------------------------------------------------------

--
-- Table structure for table `film_category`
--

CREATE TABLE `film_category` (
  `film_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `film_category`
--

INSERT INTO `film_category` (`film_id`, `category_id`) VALUES
(6, 1),
(7, 1),
(7, 3),
(8, 1),
(8, 6),
(9, 4),
(9, 8),
(9, 15),
(10, 1),
(10, 6),
(11, 3),
(11, 10),
(12, 1),
(12, 4),
(12, 5),
(13, 2),
(13, 10),
(14, 1),
(14, 6);

-- --------------------------------------------------------

--
-- Table structure for table `film_cinema`
--

CREATE TABLE `film_cinema` (
  `film_id` int(11) NOT NULL,
  `cinema_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `film_cinema`
--

INSERT INTO `film_cinema` (`film_id`, `cinema_id`) VALUES
(6, 14),
(7, 13),
(7, 14),
(7, 16),
(7, 20),
(8, 13),
(8, 16),
(8, 18),
(8, 20),
(9, 13),
(9, 15),
(9, 16),
(9, 18),
(9, 20),
(10, 14),
(10, 18),
(10, 20),
(11, 13),
(11, 20),
(12, 13),
(12, 16),
(12, 19),
(13, 21),
(14, 13),
(14, 14),
(14, 20),
(14, 21),
(14, 22),
(14, 25);

-- --------------------------------------------------------

--
-- Table structure for table `friendships`
--

CREATE TABLE `friendships` (
  `id` int(11) NOT NULL,
  `receiver_id` int(11) DEFAULT NULL,
  `sender_id` int(11) DEFAULT NULL,
  `statut` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `messenger_messages`
--

CREATE TABLE `messenger_messages` (
  `id` bigint(20) NOT NULL,
  `body` longtext NOT NULL,
  `headers` longtext NOT NULL,
  `queue_name` varchar(190) NOT NULL,
  `created_at` datetime NOT NULL,
  `available_at` datetime NOT NULL,
  `delivered_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `panier`
--

CREATE TABLE `panier` (
  `id_produit` int(11) DEFAULT NULL,
  `idpanier` int(11) NOT NULL,
  `quantite` int(11) DEFAULT NULL,
  `idClient` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `panier`
--

INSERT INTO `panier` (`id_produit`, `idpanier`, `quantite`, `idClient`) VALUES
(1, 8, 6, 113),
(19, 9, 2, 113);

-- --------------------------------------------------------

--
-- Table structure for table `participation_evenement`
--

CREATE TABLE `participation_evenement` (
  `id_participation` int(11) NOT NULL,
  `id_evenement` int(11) DEFAULT NULL,
  `id_user` int(11) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `produit`
--

CREATE TABLE `produit` (
  `id_produit` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prix` int(11) NOT NULL,
  `image` varchar(255) NOT NULL,
  `description` varchar(100) NOT NULL,
  `quantiteP` int(11) NOT NULL,
  `id_categorieProduit` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `produit`
--

INSERT INTO `produit` (`id_produit`, `nom`, `prix`, `image`, `description`, `quantiteP`, `id_categorieProduit`) VALUES
(1, 'Smoking Joker Pop Art', 50, 'img/produit/smokingjoker.jpg-6633cbcd85b2d.jpg', 'Printed on thick paper (250 g/m²)\nMaximum color brilliance and high UV resistance', 88, 2),
(2, 'Le Parrain', 170, 'img/produit/leparrain.jpg-6633cbee570e0.jpg', 'Printing on thick paper (250 g/m²)\r\nÉclat maximal des couleurs et haute résistance aux UV', 92, 2),
(3, 'OLD TIMES', 150, 'img/produit/oldtimes.jpg-6633cbff937c8.jpg', 'Limited edition (n°2/5) Sculpture,Bronze on stone\r\nDimensions Height 30cm,Width 18cm/3.00', 94, 3),
(4, 'Harry Potter', 30, 'img/produit/harrypotter1.jpg-6633cc2332708.jpg', 'Discover or rediscover the full text of J.K. Rowling with sublime color illustrations', 100, 4),
(18, 'tableau conan', 30, 'img/produit/conan.jpg-6633cc4ac1b1c.jpg', 'Printed on thick paper (250 g/m²), this artwork boasts maximum color brilliance and high UV resistan', 0, 3),
(19, 'tableau naruto', 50, 'img/produit/naruto.jpg-6633cc6096f96.jpg', 'Printed on thick paper (250 g/m²)\r\nMaximum color brilliance and high UV resistance', 0, 8);

-- --------------------------------------------------------

--
-- Table structure for table `ratingcinema`
--

CREATE TABLE `ratingcinema` (
  `id_cinema` int(11) NOT NULL,
  `id_user` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ratingfilm`
--

CREATE TABLE `ratingfilm` (
  `id_film` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `rate` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `ratingfilm`
--

INSERT INTO `ratingfilm` (`id_film`, `id_user`, `rate`) VALUES
(2, 105, 4),
(6, 105, 3),
(6, 110, 4),
(7, 110, 3),
(8, 110, 5),
(9, 110, 3),
(10, 110, 4),
(11, 110, 1),
(12, 110, 5),
(13, 113, 3);

-- --------------------------------------------------------

--
-- Table structure for table `reset_password_request`
--

CREATE TABLE `reset_password_request` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `selector` varchar(20) NOT NULL,
  `hashed_token` varchar(100) NOT NULL,
  `requested_at` datetime NOT NULL COMMENT '(DC2Type:datetime_immutable)',
  `expires_at` datetime NOT NULL COMMENT '(DC2Type:datetime_immutable)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `salle`
--

CREATE TABLE `salle` (
  `id_salle` int(11) NOT NULL,
  `id_cinema` int(11) NOT NULL,
  `nb_places` int(11) NOT NULL,
  `nom_salle` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `salle`
--

INSERT INTO `salle` (`id_salle`, `id_cinema`, `nb_places`, `nom_salle`) VALUES
(13, 13, 30, 'Room1'),
(14, 13, 25, 'Room2'),
(16, 14, 50, 'salleAli1'),
(17, 14, 50, 'salleAli2'),
(18, 15, 30, 'salle 1'),
(19, 20, 50, '50'),
(20, 21, 30, 'room1'),
(21, 21, 20, 'room10'),
(22, 25, 30, 'Room1'),
(23, 25, 40, 'Room2');

-- --------------------------------------------------------

--
-- Table structure for table `seance`
--

CREATE TABLE `seance` (
  `id_seance` int(11) NOT NULL,
  `id_film` int(11) DEFAULT NULL,
  `id_salle` int(11) DEFAULT NULL,
  `id_cinema` int(11) DEFAULT NULL,
  `HD` time DEFAULT NULL,
  `HF` time DEFAULT NULL,
  `date` date DEFAULT NULL,
  `prix` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `seance`
--

INSERT INTO `seance` (`id_seance`, `id_film`, `id_salle`, `id_cinema`, `HD`, `HF`, `date`, `prix`) VALUES
(1, 6, 16, 14, '16:00:00', '18:00:00', '2024-05-04', 16),
(2, 6, 13, 14, '01:29:12', '02:29:12', '2024-05-01', 28),
(7, 8, 18, 15, '19:48:31', '22:48:31', '2024-05-09', 20),
(8, 12, 13, 13, '11:00:00', '13:30:00', '2024-05-11', 20),
(10, 13, 21, 21, '12:00:00', '15:00:00', '2024-05-10', 30),
(11, 14, 22, 25, '15:00:00', '17:00:00', '2024-05-07', 30);

-- --------------------------------------------------------

--
-- Table structure for table `seat`
--

CREATE TABLE `seat` (
  `id` int(11) NOT NULL,
  `id_salle` int(11) NOT NULL,
  `statut` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `seat`
--

INSERT INTO `seat` (`id`, `id_salle`, `statut`) VALUES
(4884, 13, 'vide'),
(4885, 13, 'reserve'),
(4886, 13, 'reserve'),
(4887, 13, 'reserve'),
(4888, 13, 'vide'),
(4889, 13, 'vide'),
(4890, 13, 'vide'),
(4891, 13, 'vide'),
(4892, 13, 'vide'),
(4893, 13, 'vide'),
(4894, 13, 'vide'),
(4895, 13, 'vide'),
(4896, 13, 'vide'),
(4897, 13, 'vide'),
(4898, 13, 'vide'),
(4899, 13, 'vide'),
(4900, 13, 'vide'),
(4901, 13, 'vide'),
(4902, 13, 'vide'),
(4903, 13, 'vide'),
(4904, 13, 'vide'),
(4905, 13, 'vide'),
(4906, 13, 'vide'),
(4907, 13, 'vide'),
(4908, 13, 'vide'),
(4909, 13, 'vide'),
(4910, 13, 'vide'),
(4911, 13, 'vide'),
(4912, 13, 'vide'),
(4913, 13, 'vide'),
(4914, 14, 'vide'),
(4915, 14, 'vide'),
(4916, 14, 'vide'),
(4917, 14, 'vide'),
(4918, 14, 'vide'),
(4919, 14, 'vide'),
(4920, 14, 'vide'),
(4921, 14, 'vide'),
(4922, 14, 'vide'),
(4923, 14, 'vide'),
(4924, 14, 'vide'),
(4925, 14, 'vide'),
(4926, 14, 'vide'),
(4927, 14, 'vide'),
(4928, 14, 'vide'),
(4929, 14, 'vide'),
(4930, 14, 'vide'),
(4931, 14, 'vide'),
(4932, 14, 'vide'),
(4933, 14, 'vide'),
(5034, 16, 'vide'),
(5035, 16, 'vide'),
(5036, 16, 'reserve'),
(5037, 16, 'vide'),
(5038, 16, 'vide'),
(5039, 16, 'vide'),
(5040, 16, 'vide'),
(5041, 16, 'reserve'),
(5042, 16, 'vide'),
(5043, 16, 'vide'),
(5044, 16, 'vide'),
(5045, 16, 'vide'),
(5046, 16, 'reserve'),
(5047, 16, 'reserve'),
(5048, 16, 'vide'),
(5049, 16, 'vide'),
(5050, 16, 'vide'),
(5051, 16, 'reserve'),
(5052, 16, 'vide'),
(5053, 16, 'vide'),
(5054, 16, 'reserve'),
(5055, 16, 'reserve'),
(5056, 16, 'reserve'),
(5057, 16, 'reserve'),
(5058, 16, 'vide'),
(5059, 16, 'vide'),
(5060, 16, 'reserve'),
(5061, 16, 'reserve'),
(5062, 16, 'reserve'),
(5063, 16, 'vide'),
(5064, 16, 'reserve'),
(5065, 16, 'reserve'),
(5066, 16, 'reserve'),
(5067, 16, 'reserve'),
(5068, 16, 'reserve'),
(5069, 16, 'vide'),
(5070, 16, 'reserve'),
(5071, 16, 'reserve'),
(5072, 16, 'reserve'),
(5073, 16, 'vide'),
(5074, 16, 'reserve'),
(5075, 16, 'reserve'),
(5076, 16, 'reserve'),
(5077, 16, 'vide'),
(5078, 16, 'vide'),
(5079, 16, 'reserve'),
(5080, 16, 'reserve'),
(5081, 16, 'reserve'),
(5082, 16, 'vide'),
(5083, 16, 'vide'),
(5084, 17, 'vide'),
(5085, 17, 'vide'),
(5086, 17, 'vide'),
(5087, 17, 'vide'),
(5088, 17, 'vide'),
(5089, 17, 'vide'),
(5090, 17, 'vide'),
(5091, 17, 'vide'),
(5092, 17, 'vide'),
(5093, 17, 'vide'),
(5094, 17, 'vide'),
(5095, 17, 'vide'),
(5096, 17, 'vide'),
(5097, 17, 'vide'),
(5098, 17, 'vide'),
(5099, 17, 'vide'),
(5100, 17, 'vide'),
(5101, 17, 'vide'),
(5102, 17, 'vide'),
(5103, 17, 'vide'),
(5104, 17, 'vide'),
(5105, 17, 'vide'),
(5106, 17, 'vide'),
(5107, 17, 'vide'),
(5108, 17, 'vide'),
(5109, 17, 'vide'),
(5110, 17, 'vide'),
(5111, 17, 'vide'),
(5112, 17, 'vide'),
(5113, 17, 'vide'),
(5114, 17, 'vide'),
(5115, 17, 'vide'),
(5116, 17, 'vide'),
(5117, 17, 'vide'),
(5118, 17, 'vide'),
(5119, 17, 'vide'),
(5120, 17, 'vide'),
(5121, 17, 'vide'),
(5122, 17, 'vide'),
(5123, 17, 'vide'),
(5124, 17, 'vide'),
(5125, 17, 'vide'),
(5126, 17, 'vide'),
(5127, 17, 'vide'),
(5128, 17, 'vide'),
(5129, 17, 'vide'),
(5130, 17, 'vide'),
(5131, 17, 'vide'),
(5132, 17, 'vide'),
(5133, 17, 'vide'),
(5134, 20, 'vide'),
(5135, 20, 'vide'),
(5136, 20, 'vide'),
(5137, 20, 'vide'),
(5138, 20, 'vide'),
(5139, 20, 'vide'),
(5140, 20, 'vide'),
(5141, 20, 'vide'),
(5142, 20, 'vide'),
(5143, 20, 'vide'),
(5144, 20, 'vide'),
(5145, 20, 'vide'),
(5146, 20, 'vide'),
(5147, 20, 'vide'),
(5148, 20, 'vide'),
(5149, 20, 'vide'),
(5150, 20, 'vide'),
(5151, 20, 'vide'),
(5152, 20, 'vide'),
(5153, 20, 'vide'),
(5154, 20, 'vide'),
(5155, 20, 'vide'),
(5156, 20, 'vide'),
(5157, 20, 'vide'),
(5158, 20, 'vide'),
(5159, 20, 'vide'),
(5160, 20, 'vide'),
(5161, 20, 'vide'),
(5162, 20, 'vide'),
(5163, 20, 'vide'),
(5164, 21, 'vide'),
(5165, 21, 'vide'),
(5166, 21, 'vide'),
(5167, 21, 'vide'),
(5168, 21, 'vide'),
(5169, 21, 'vide'),
(5170, 21, 'vide'),
(5171, 21, 'reserve'),
(5172, 21, 'vide'),
(5173, 21, 'vide'),
(5174, 21, 'vide'),
(5175, 21, 'vide'),
(5176, 21, 'reserve'),
(5177, 21, 'reserve'),
(5178, 21, 'reserve'),
(5179, 21, 'vide'),
(5180, 21, 'vide'),
(5181, 21, 'vide'),
(5182, 21, 'vide'),
(5183, 21, 'vide'),
(5184, 22, 'vide'),
(5185, 22, 'vide'),
(5186, 22, 'vide'),
(5187, 22, 'vide'),
(5188, 22, 'vide'),
(5189, 22, 'vide'),
(5190, 22, 'vide'),
(5191, 22, 'vide'),
(5192, 22, 'vide'),
(5193, 22, 'vide'),
(5194, 22, 'vide'),
(5195, 22, 'vide'),
(5196, 22, 'vide'),
(5197, 22, 'vide'),
(5198, 22, 'vide'),
(5199, 22, 'vide'),
(5200, 22, 'vide'),
(5201, 22, 'vide'),
(5202, 22, 'vide'),
(5203, 22, 'vide'),
(5204, 22, 'vide'),
(5205, 22, 'vide'),
(5206, 22, 'vide'),
(5207, 22, 'vide'),
(5208, 22, 'vide'),
(5209, 22, 'vide'),
(5210, 22, 'vide'),
(5211, 22, 'vide'),
(5212, 22, 'vide'),
(5213, 22, 'vide'),
(5214, 23, 'vide'),
(5215, 23, 'vide'),
(5216, 23, 'vide'),
(5217, 23, 'vide'),
(5218, 23, 'vide'),
(5219, 23, 'vide'),
(5220, 23, 'vide'),
(5221, 23, 'vide'),
(5222, 23, 'vide'),
(5223, 23, 'vide'),
(5224, 23, 'vide'),
(5225, 23, 'vide'),
(5226, 23, 'vide'),
(5227, 23, 'vide'),
(5228, 23, 'vide'),
(5229, 23, 'vide'),
(5230, 23, 'vide'),
(5231, 23, 'vide'),
(5232, 23, 'vide'),
(5233, 23, 'vide'),
(5234, 23, 'vide'),
(5235, 23, 'vide'),
(5236, 23, 'vide'),
(5237, 23, 'vide'),
(5238, 23, 'vide'),
(5239, 23, 'vide'),
(5240, 23, 'vide'),
(5241, 23, 'vide'),
(5242, 23, 'vide'),
(5243, 23, 'vide'),
(5244, 23, 'vide'),
(5245, 23, 'vide'),
(5246, 23, 'vide'),
(5247, 23, 'vide'),
(5248, 23, 'vide'),
(5249, 23, 'vide'),
(5250, 23, 'vide'),
(5251, 23, 'vide'),
(5252, 23, 'vide'),
(5253, 23, 'vide');

-- --------------------------------------------------------

--
-- Table structure for table `series`
--

CREATE TABLE `series` (
  `idserie` int(11) NOT NULL,
  `nom` varchar(30) NOT NULL,
  `resume` varchar(50) NOT NULL,
  `directeur` varchar(50) NOT NULL,
  `pays` varchar(50) NOT NULL,
  `image` varchar(255) NOT NULL,
  `liked` int(11) DEFAULT NULL,
  `nbLikes` int(11) DEFAULT NULL,
  `disliked` int(11) DEFAULT NULL,
  `nbDislikes` int(11) DEFAULT NULL,
  `idcategorie` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `series`
--

INSERT INTO `series` (`idserie`, `nom`, `resume`, `directeur`, `pays`, `image`, `liked`, `nbLikes`, `disliked`, `nbDislikes`, `idcategorie`) VALUES
(2, 'Peaky blinders', 'A gangster family epic set in 1900s England', 'Matt and Ross Duffer', 'Birmingham, England,', 'img/series/wmunrwzmgkgfrclabhrrzd1jf49-scaled-6634cf69d1c6b.jpg', 0, 1, 0, 1, 1),
(4, 'Stranger Things', 'Centers around the residents of the fictional town', 'Matt and Ross Duffer', 'United States', 'img/series/9781532143892-66360efa12050.jpg', 0, 0, 0, 0, 3),
(5, 'Friends', 'navigates romantic relationships, career challenge', 'Julie Andem', 'United States', 'img/series/2e98c58c758e3080a2cc84682cd66e4b-66360f93123fb.jpg', 0, 1, 0, 0, 4),
(6, 'Riverdale', 'the teenagers grapple with issues such friendship', 'Roberto Aguirre-Sacasa', 'United States', 'img/series/138995-663612e9c46b3.jpg', 0, 0, 0, 0, 1),
(7, 'You', 'he show explores themes of privacy, social media', 'Greg Berlanti', 'United States', 'img/series/you-6636107863249.webp', 0, 0, 0, 0, 1),
(8, 'Lucifer', 'blends elements of crime procedural and fantasy', 'Baran bo Odar', 'United States', 'img/series/lucifer-663610fac8633.jpg', 0, 0, 0, 0, 1),
(9, 'Skam France', 'five high-school girls who learn about friendship', 'Julie Andem', 'French', 'img/series/images_2-663611407eedd.jpg', 0, 0, 0, 0, 1),
(11, 'Fraiser', 'Frasier\" revolves around Frasier\'s  pretentiousnes', 'David Lee.', 'United States.', 'img/series/1697025-663611d3c4350.webp', 0, 0, 0, 0, 3),
(12, 'Glitch', 'The series follows local police officer James', 'Tony Krawitz', 'Australia', 'img/series/glitch_tv_show_australian_series_netflix-6636122661186.jpg', 0, 0, 0, 0, 3),
(13, 'Emily in Paris', 'an American college graduate and moves to paris', 'Andrew Fleming', 'United States', 'img/series/season-1-663614ad62916.jpg', 0, 0, 0, 0, 4);

-- --------------------------------------------------------

--
-- Table structure for table `sponsor`
--

CREATE TABLE `sponsor` (
  `ID` int(11) NOT NULL,
  `nomSociete` varchar(50) NOT NULL,
  `Logo` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `sponsor`
--

INSERT INTO `sponsor` (`ID`, `nomSociete`, `Logo`) VALUES
(1, 'Apple', 'https://images.unsplash.com/photo-1612994370726-5d4d609fca1b?q=80&w=1964&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
(2, 'Jaguar', 'https://images.unsplash.com/photo-1562783912-21ad31ee2a83?q=80&w=2148&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D');

-- --------------------------------------------------------

--
-- Table structure for table `ticket`
--

CREATE TABLE `ticket` (
  `id_user` int(11) NOT NULL,
  `id_seance` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `num_telephone` int(11) DEFAULT NULL,
  `password` varchar(180) NOT NULL,
  `role` varchar(50) NOT NULL,
  `adresse` varchar(50) DEFAULT NULL,
  `date_de_naissance` date DEFAULT NULL,
  `email` varchar(180) NOT NULL,
  `photo_de_profil` varchar(255) DEFAULT NULL,
  `is_verified` tinyint(1) NOT NULL,
  `roles` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '(DC2Type:json)' CHECK (json_valid(`roles`)),
  `totp_secret` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `nom`, `prenom`, `num_telephone`, `password`, `role`, `adresse`, `date_de_naissance`, `email`, `photo_de_profil`, `is_verified`, `roles`, `totp_secret`) VALUES
(111, 'ALI', 'AMMARI', NULL, '$2y$13$ipImRVOd141Q5olJ3nR19eF8VZRAdvZnuDDyX0fK8e9FHVyR5MBYe', 'client', NULL, NULL, 'aliammari862002@gmail.com', '/img/users/27266.jpg', 1, '[\"ROLE_CLIENT\"]', NULL),
(112, 'ghaya', 'Ghaya', 29201398, '$2y$13$JP2NutiDRtCIiz0TkVXzc.umKhnIkjJ32.8bNV1BZyYzkzBX0UU9G', 'client', 'bennane', '2001-12-08', 'ghaya.sassi2@gmail.tn', '/img/users/92890.jpg', 0, '[\"ROLE_CLIENT\"]', NULL),
(113, 'Sassi', 'Ghaya', 29201398, '$2y$13$Zi2JyIWyaeB7pAgtWEKK6uGkXynQOxmpZ53BRolmEWPXHOgYJMh36', 'client', 'bennane', '2001-12-08', 'ghaya.sassi3@gmail.tn', '/img/users/27266.jpg', 1, '[\"ROLE_CLIENT\"]', NULL),
(114, 'hajlaoui', 'loujain', 98962361, '$2y$13$x9ygtL0sZQbOwy2nI9ZDdOyCyYKwKxhjk7MdXMgVJ7doHWRfIpb/6', 'responsable de cinema', 'lycee 9 avril sidi bouzid', '2023-02-06', 'loujainhajlaoui@gmail.com', '/img/users/70865.jpg', 0, '[\"ROLE_RESPONSABLE_DE_CINEMA\"]', NULL),
(115, 'Hajkacem', 'Ahmed', 21000000, '$2y$13$Fn1PeeCzYbSXA2wrlQkykex8SYzkHJ/Fiz.EYCr4akuHDsz/E3UdG', 'client', 'ariana soghra', '2024-02-14', 'ahmed.hajkacem1@gmail.com', '/img/users/53594.jpg', 0, '[\"ROLE_CLIENT\"]', NULL),
(116, 'Wolfe', 'Darius', 109, '$2y$13$sBqkFSq6u7IM5QJX5ZCFJu4CY9FYt0BhMsTNoHn3wpclbvLwUR6Fq', 'admin', 'Qui odio dolor conse', '2002-07-27', 'ammari.ali.0001@gmail.com', '/img/users/ali.jpg', 1, '[\"ROLE_ADMIN\"]', NULL),
(117, 'Hajkasem', 'Ahmad', 22000000, '$2y$13$XXjW32GWOt63g.1aAMfHaeb00FCzQLrd8bnzN7inijq6w3BO49IFC', 'responsable de cinema', 'ariana soghra', '2024-04-18', 'ahmed.haj.kacem1@gmail.com', '/img/users/91514.jpg', 0, '[\"ROLE_RESPONSABLE_DE_CINEMA\"]', NULL),
(118, 'ghaya', 'sassi', 29201398, '$2y$13$jaAhU7vaQQLd8XGa6/MBZuGqtI8CVh00aCgYXdnCTNm9FeBB6Wv7S', 'responsable de cinema', 'Bennane', '2001-12-08', 'gh@gmail.com', '/img/users/22881.jpg', 1, '[\"ROLE_RESPONSABLE_DE_CINEMA\"]', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `actor`
--
ALTER TABLE `actor`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `actorfilm`
--
ALTER TABLE `actorfilm`
  ADD PRIMARY KEY (`idactor`,`idfilm`),
  ADD KEY `fk_idfilm` (`idfilm`);

--
-- Indexes for table `avis`
--
ALTER TABLE `avis`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_clients` (`idusers`),
  ADD KEY `id_produit` (`id_produit`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`idcategorie`);

--
-- Indexes for table `categorie_evenement`
--
ALTER TABLE `categorie_evenement`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `categorie_produit`
--
ALTER TABLE `categorie_produit`
  ADD PRIMARY KEY (`id_categorie`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nom` (`nom`);

--
-- Indexes for table `cinema`
--
ALTER TABLE `cinema`
  ADD PRIMARY KEY (`id_cinema`);

--
-- Indexes for table `commande`
--
ALTER TABLE `commande`
  ADD PRIMARY KEY (`idCommande`),
  ADD KEY `fk_idClient` (`idClient`);

--
-- Indexes for table `commandeitem`
--
ALTER TABLE `commandeitem`
  ADD PRIMARY KEY (`idCommandeItem`),
  ADD KEY `fk_produit` (`id_produit`),
  ADD KEY `fk_commande` (`idCommande`);

--
-- Indexes for table `commentaire`
--
ALTER TABLE `commentaire`
  ADD PRIMARY KEY (`idcommentaire`),
  ADD KEY `fk_clients_comment_1` (`idClient`);

--
-- Indexes for table `commentairecinema`
--
ALTER TABLE `commentairecinema`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_user` (`idclient`),
  ADD KEY `fk_cinema` (`idcinema`);

--
-- Indexes for table `commentaire_produit`
--
ALTER TABLE `commentaire_produit`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_5A6D7E7499DED506` (`id_client_id`),
  ADD KEY `IDX_5A6D7E74F7384557` (`id_produit`);

--
-- Indexes for table `doctrine_migration_versions`
--
ALTER TABLE `doctrine_migration_versions`
  ADD PRIMARY KEY (`version`);

--
-- Indexes for table `episodes`
--
ALTER TABLE `episodes`
  ADD PRIMARY KEY (`idepisode`),
  ADD KEY `idserie` (`idserie`);

--
-- Indexes for table `evenement`
--
ALTER TABLE `evenement`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `cle_secondaire` (`id_categorie`);

--
-- Indexes for table `favoris`
--
ALTER TABLE `favoris`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_fav_serie` (`id_serie`),
  ADD KEY `fk_fav_user` (`id_user`);

--
-- Indexes for table `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_feedback_episode` (`id_episode`),
  ADD KEY `fk_feedback_user` (`id_user`);

--
-- Indexes for table `feedback_evenement`
--
ALTER TABLE `feedback_evenement`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `fk_event_11` (`id_evenement`),
  ADD KEY `FK_user_feed` (`id_user`);

--
-- Indexes for table `film`
--
ALTER TABLE `film`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `filmcategory`
--
ALTER TABLE `filmcategory`
  ADD PRIMARY KEY (`film_id`,`category_id`),
  ADD KEY `fk_filmCategorie_2` (`category_id`),
  ADD KEY `fk_filmCategorie_1` (`film_id`);

--
-- Indexes for table `filmcoment`
--
ALTER TABLE `filmcoment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `pk_comment_film` (`film_id`),
  ADD KEY `pk_comment_user` (`user_id`);

--
-- Indexes for table `film_actor`
--
ALTER TABLE `film_actor`
  ADD PRIMARY KEY (`film_id`,`actor_id`),
  ADD KEY `IDX_DD19A8A9567F5183` (`film_id`),
  ADD KEY `IDX_DD19A8A910DAF24A` (`actor_id`);

--
-- Indexes for table `film_category`
--
ALTER TABLE `film_category`
  ADD PRIMARY KEY (`film_id`,`category_id`),
  ADD KEY `IDX_A4CBD6A8567F5183` (`film_id`),
  ADD KEY `IDX_A4CBD6A812469DE2` (`category_id`);

--
-- Indexes for table `film_cinema`
--
ALTER TABLE `film_cinema`
  ADD PRIMARY KEY (`film_id`,`cinema_id`),
  ADD KEY `IDX_BF7139BE567F5183` (`film_id`),
  ADD KEY `IDX_BF7139BEB4CB84B6` (`cinema_id`);

--
-- Indexes for table `friendships`
--
ALTER TABLE `friendships`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_E0A8B7CACD53EDB6` (`receiver_id`),
  ADD KEY `IDX_E0A8B7CAF624B39D` (`sender_id`);

--
-- Indexes for table `messenger_messages`
--
ALTER TABLE `messenger_messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_75EA56E0FB7336F0` (`queue_name`),
  ADD KEY `IDX_75EA56E0E3BD61CE` (`available_at`),
  ADD KEY `IDX_75EA56E016BA31DB` (`delivered_at`);

--
-- Indexes for table `panier`
--
ALTER TABLE `panier`
  ADD PRIMARY KEY (`idpanier`),
  ADD KEY `FK_pro` (`id_produit`),
  ADD KEY `FK_user` (`idClient`);

--
-- Indexes for table `participation_evenement`
--
ALTER TABLE `participation_evenement`
  ADD PRIMARY KEY (`id_participation`),
  ADD KEY `cle_secondaire2` (`id_user`),
  ADD KEY `cle_secondaire1` (`id_evenement`);

--
-- Indexes for table `produit`
--
ALTER TABLE `produit`
  ADD PRIMARY KEY (`id_produit`),
  ADD KEY `fk_categories` (`id_categorieProduit`);

--
-- Indexes for table `ratingcinema`
--
ALTER TABLE `ratingcinema`
  ADD PRIMARY KEY (`id_cinema`,`id_user`),
  ADD KEY `IDX_C3ADE714F2B4B159` (`id_cinema`),
  ADD KEY `IDX_C3ADE7146B3CA4B` (`id_user`);

--
-- Indexes for table `ratingfilm`
--
ALTER TABLE `ratingfilm`
  ADD PRIMARY KEY (`id_film`,`id_user`),
  ADD KEY `fk_user_ratin` (`id_user`),
  ADD KEY `fk_film_rating` (`id_film`);

--
-- Indexes for table `reset_password_request`
--
ALTER TABLE `reset_password_request`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_7CE748AA76ED395` (`user_id`);

--
-- Indexes for table `salle`
--
ALTER TABLE `salle`
  ADD PRIMARY KEY (`id_salle`),
  ADD KEY `fk_cinema_salle` (`id_cinema`);

--
-- Indexes for table `seance`
--
ALTER TABLE `seance`
  ADD PRIMARY KEY (`id_seance`),
  ADD KEY `fk_cinema_seance` (`id_cinema`),
  ADD KEY `fk_film_seance` (`id_film`),
  ADD KEY `fk_salle_seance` (`id_salle`);

--
-- Indexes for table `seat`
--
ALTER TABLE `seat`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_3D5C3666A0123F6C` (`id_salle`);

--
-- Indexes for table `series`
--
ALTER TABLE `series`
  ADD PRIMARY KEY (`idserie`),
  ADD KEY `idcategorie` (`idcategorie`);

--
-- Indexes for table `sponsor`
--
ALTER TABLE `sponsor`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `ticket`
--
ALTER TABLE `ticket`
  ADD PRIMARY KEY (`id_user`,`id_seance`),
  ADD KEY `IDX_97A0ADA36B3CA4B` (`id_user`),
  ADD KEY `IDX_97A0ADA3F94A48E3` (`id_seance`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UNIQ_1483A5E9E7927C74` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `actor`
--
ALTER TABLE `actor`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `avis`
--
ALTER TABLE `avis`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `idcategorie` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `categorie_evenement`
--
ALTER TABLE `categorie_evenement`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `categorie_produit`
--
ALTER TABLE `categorie_produit`
  MODIFY `id_categorie` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `cinema`
--
ALTER TABLE `cinema`
  MODIFY `id_cinema` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `commande`
--
ALTER TABLE `commande`
  MODIFY `idCommande` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;

--
-- AUTO_INCREMENT for table `commandeitem`
--
ALTER TABLE `commandeitem`
  MODIFY `idCommandeItem` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `commentaire`
--
ALTER TABLE `commentaire`
  MODIFY `idcommentaire` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `commentairecinema`
--
ALTER TABLE `commentairecinema`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `commentaire_produit`
--
ALTER TABLE `commentaire_produit`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `episodes`
--
ALTER TABLE `episodes`
  MODIFY `idepisode` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `evenement`
--
ALTER TABLE `evenement`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `favoris`
--
ALTER TABLE `favoris`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `feedback`
--
ALTER TABLE `feedback`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `feedback_evenement`
--
ALTER TABLE `feedback_evenement`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `film`
--
ALTER TABLE `film`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `filmcoment`
--
ALTER TABLE `filmcoment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `friendships`
--
ALTER TABLE `friendships`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `messenger_messages`
--
ALTER TABLE `messenger_messages`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `panier`
--
ALTER TABLE `panier`
  MODIFY `idpanier` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `participation_evenement`
--
ALTER TABLE `participation_evenement`
  MODIFY `id_participation` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `produit`
--
ALTER TABLE `produit`
  MODIFY `id_produit` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `reset_password_request`
--
ALTER TABLE `reset_password_request`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `salle`
--
ALTER TABLE `salle`
  MODIFY `id_salle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `seance`
--
ALTER TABLE `seance`
  MODIFY `id_seance` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `seat`
--
ALTER TABLE `seat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5254;

--
-- AUTO_INCREMENT for table `series`
--
ALTER TABLE `series`
  MODIFY `idserie` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `sponsor`
--
ALTER TABLE `sponsor`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=119;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `avis`
--
ALTER TABLE `avis`
  ADD CONSTRAINT `FK_8F91ABF0C286C9F0` FOREIGN KEY (`idusers`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FK_8F91ABF0F7384557` FOREIGN KEY (`id_produit`) REFERENCES `produit` (`id_produit`);

--
-- Constraints for table `commande`
--
ALTER TABLE `commande`
  ADD CONSTRAINT `FK_6EEAA67DA455ACCF` FOREIGN KEY (`idClient`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `commandeitem`
--
ALTER TABLE `commandeitem`
  ADD CONSTRAINT `FK_55202BB43D498C26` FOREIGN KEY (`idCommande`) REFERENCES `commande` (`idCommande`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_55202BB4F7384557` FOREIGN KEY (`id_produit`) REFERENCES `produit` (`id_produit`);

--
-- Constraints for table `commentaire`
--
ALTER TABLE `commentaire`
  ADD CONSTRAINT `FK_67F068BCA455ACCF` FOREIGN KEY (`idClient`) REFERENCES `users` (`id`);

--
-- Constraints for table `commentaire_produit`
--
ALTER TABLE `commentaire_produit`
  ADD CONSTRAINT `FK_5A6D7E7499DED506` FOREIGN KEY (`id_client_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_5A6D7E74F7384557` FOREIGN KEY (`id_produit`) REFERENCES `produit` (`id_produit`);

--
-- Constraints for table `episodes`
--
ALTER TABLE `episodes`
  ADD CONSTRAINT `FK_7DD55EDD7C3FFF2D` FOREIGN KEY (`idserie`) REFERENCES `series` (`idserie`);

--
-- Constraints for table `evenement`
--
ALTER TABLE `evenement`
  ADD CONSTRAINT `FK_B26681EC9486A13` FOREIGN KEY (`id_categorie`) REFERENCES `categorie_evenement` (`ID`);

--
-- Constraints for table `feedback_evenement`
--
ALTER TABLE `feedback_evenement`
  ADD CONSTRAINT `FK_B56236A56B3CA4B` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FK_B56236A58B13D439` FOREIGN KEY (`id_evenement`) REFERENCES `evenement` (`ID`);

--
-- Constraints for table `film_actor`
--
ALTER TABLE `film_actor`
  ADD CONSTRAINT `FK_DD19A8A910DAF24A` FOREIGN KEY (`actor_id`) REFERENCES `actor` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_DD19A8A9567F5183` FOREIGN KEY (`film_id`) REFERENCES `film` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `film_category`
--
ALTER TABLE `film_category`
  ADD CONSTRAINT `FK_A4CBD6A812469DE2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_A4CBD6A8567F5183` FOREIGN KEY (`film_id`) REFERENCES `film` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `film_cinema`
--
ALTER TABLE `film_cinema`
  ADD CONSTRAINT `FK_BF7139BE567F5183` FOREIGN KEY (`film_id`) REFERENCES `film` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_BF7139BEB4CB84B6` FOREIGN KEY (`cinema_id`) REFERENCES `cinema` (`id_cinema`);

--
-- Constraints for table `friendships`
--
ALTER TABLE `friendships`
  ADD CONSTRAINT `FK_E0A8B7CACD53EDB6` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FK_E0A8B7CAF624B39D` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `panier`
--
ALTER TABLE `panier`
  ADD CONSTRAINT `FK_24CC0DF2A455ACCF` FOREIGN KEY (`idClient`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_24CC0DF2F7384557` FOREIGN KEY (`id_produit`) REFERENCES `produit` (`id_produit`);

--
-- Constraints for table `participation_evenement`
--
ALTER TABLE `participation_evenement`
  ADD CONSTRAINT `FK_65A146758B13D439` FOREIGN KEY (`id_evenement`) REFERENCES `evenement` (`ID`);

--
-- Constraints for table `produit`
--
ALTER TABLE `produit`
  ADD CONSTRAINT `FK_29A5EC277A87CDD0` FOREIGN KEY (`id_categorieProduit`) REFERENCES `categorie_produit` (`id_categorie`);

--
-- Constraints for table `ratingcinema`
--
ALTER TABLE `ratingcinema`
  ADD CONSTRAINT `FK_C3ADE7146B3CA4B` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FK_C3ADE714F2B4B159` FOREIGN KEY (`id_cinema`) REFERENCES `cinema` (`id_cinema`);

--
-- Constraints for table `reset_password_request`
--
ALTER TABLE `reset_password_request`
  ADD CONSTRAINT `FK_7CE748AA76ED395` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `salle`
--
ALTER TABLE `salle`
  ADD CONSTRAINT `FK_4E977E5CF2B4B159` FOREIGN KEY (`id_cinema`) REFERENCES `cinema` (`id_cinema`) ON DELETE CASCADE;

--
-- Constraints for table `seance`
--
ALTER TABLE `seance`
  ADD CONSTRAINT `FK_DF7DFD0E964A220` FOREIGN KEY (`id_film`) REFERENCES `film` (`id`),
  ADD CONSTRAINT `FK_DF7DFD0EA0123F6C` FOREIGN KEY (`id_salle`) REFERENCES `salle` (`id_salle`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_DF7DFD0EF2B4B159` FOREIGN KEY (`id_cinema`) REFERENCES `cinema` (`id_cinema`);

--
-- Constraints for table `seat`
--
ALTER TABLE `seat`
  ADD CONSTRAINT `FK_3D5C3666A0123F6C` FOREIGN KEY (`id_salle`) REFERENCES `salle` (`id_salle`) ON DELETE CASCADE;

--
-- Constraints for table `series`
--
ALTER TABLE `series`
  ADD CONSTRAINT `FK_3A10012D37667FC1` FOREIGN KEY (`idcategorie`) REFERENCES `categories` (`idcategorie`);

--
-- Constraints for table `ticket`
--
ALTER TABLE `ticket`
  ADD CONSTRAINT `FK_97A0ADA36B3CA4B` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FK_97A0ADA3F94A48E3` FOREIGN KEY (`id_seance`) REFERENCES `seance` (`id_seance`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
