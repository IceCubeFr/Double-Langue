import extensions.File;
import extensions.CSVFile;

class Main extends Program {

    // Variables et constantes globales ---------------------------------------------------------------------------------------

    final String QUESTIONS_PATH = "./ressources/questions.csv";
    final String TRAINING_LEVELS_PATH = "./ressources/trainingLevels.csv";
    final String PLAYERS_PATH = "./files/players.csv";
    final String CUSTOM_LEVELS_PATH = "./files/customLevels.csv";
    final String DRAPEAU_PATH = "./ressources/drapeau_en.txt";
    final String DIALOGUES_PATH = "./ressources/dialogues.txt";
    final String PIERRE_PATH = "./ressources/pierre.txt";
    final String VICTORY_PATH = "./ressources/victory.txt";
    final String DEFEAT_PATH = "./ressources/defeat.txt";
    final String RULES_PATH = "./ressources/rules.txt";
    final String ANSI_GREY = "\u001b[90m";
    final String FEY_LA_FEE = ANSI_PURPLE + "Fey la fée" + ANSI_RESET;

    boolean loadedSuccessfully = true;

    int[][] trainingLevels = new int[0][0];
    int[][] customLevels = new int[0][0];

    Question[] questionList = new Question[0];

    Player[] playerList = new Player[0];

    String[][] dialogues = new String[0][0];

    Player actualPlayer;

    // Fonctions de convertion de type ---------------------------------------------------------------------------------------

    QuestionType stringToQuestionType(String type) {
        /**
         * Renvoie l'enum QuestionType correspondant au type de question mit en paramètre au format chaîne de caractère
         * 
         * Elle permet la conversion d'un type String à un type QuestionType, principalement pour l'initialisation d'un fichier CSV
         * 
         * @param type : Type de question au format chaîne de caractère
         * @return Type de question au format QuestionType
         */

        if(equals(type, "QCM")) {
            return QuestionType.QCM;
        } else if (equals(type, "INPUT")) {
            return QuestionType.INPUT;
        } else {
            return null;
        }
    }

    void testStringToQuestionType() {
        assertEquals(QuestionType.QCM, stringToQuestionType("QCM"));
        assertEquals(QuestionType.INPUT, stringToQuestionType("INPUT"));
    }

    String questionTypeToString(QuestionType type) {
        /**
         * Renvoie la chaîne de caractère correspondant au type de Question, la chaîne vide si aucun ne correspond.
         * 
         * Elle permet la conversion d'un type QuestionType à un type String, principalement pour la sauvegarde en CSV
         * 
         * @param type : Type de question au format QuestionType
         * @return Type de question au format chaîne de caractère
         */
        if(type == QuestionType.QCM) {
            return "QCM";
        } else if (type == QuestionType.INPUT) {
            return "INPUT";
        } else {
            return "";
        }
    }

    void testQuestionTypeToString() {
        assertEquals("QCM", questionTypeToString(QuestionType.QCM));
        assertEquals("INPUT", questionTypeToString(QuestionType.INPUT));
    }
    
    // Fonctions d'affichage ---------------------------------------------------------------------------------------

    void affichageDrapeau() {
        /**
         * Affiche le drapeau britannique
         * Le drapeau est stocké dans le dossier ressources au format .txt
         */
        clearScreen();
        extensions.File f = newFile(DRAPEAU_PATH);
        while(ready(f)) {
            println(readLine(f));
        }
    }

    void affichagePierreTombale() {
        /**
         * Affiche la pierre tombale de la fin du mode histoire
         * La pierre est stockée dans le dossier ressources au format .txt
         */
        extensions.File f = newFile(PIERRE_PATH);
        while(ready(f)) {
            println(readLine(f));
        }
    }

    void affichageText(String txt) {
        /**
         * Affiche le texte mit en paramètre avec un temps d'attente par défaut de 1000 ms à la fin de l'affichage, de 10 ms à la fin de chaque caractère et avec un retour à la ligne
         * 
         * Fonction d'affichage principale du jeu, elle ajoute un délai de 50 ms entre l'affichage de chaque caractère, permettant un effet de machine à écrire
         * 
         * Cette fonction est une surcharge de la fonction affichageText(txt, delaiChar, delaiFin, retourALaLigne)
         * 
         * @param txt : Texte à afficher
         * @see affichageText(txt, delaiChar, delaiFin, retourALaLigne)
         */
        affichageText(txt, 10, 1000, true);
    }

    void affichageText(String txt, int delaiFin) {
        /**
         * Affiche le texte mit en paramètre avec un délai à la fin de l'affichage et avec un retour à la ligne
         * 
         * Il s'agit d'une surcharge de la fonction afficherText(String)
         * 
         * @param txt : Texte à afficher
         * @param delai : Délai à la fin de l'affichage
         * @see affichageText(txt, delaiChar, delaiFin, retourALaLigne)
         */
        affichageText(txt, 10, delaiFin, true);
    }

    void affichageText(String txt, int delaiFin, boolean retourALaLigne) {
        /**
         * Affiche le texte mit en paramètre avec un délai à la fin de l'affichage définit en paramètre et avec la possibilité de choisir un retour à la ligne ou non
         * 
         * Il s'agit d'une surcharge de la fonction afficherText(String)
         * 
         * @param txt : Texte à afficher
         * @param delai : Délai à la fin de l'affichage
         * @param retourALaLigne : Définit s'il y aura un retour à la ligne à la fin de l'affichage
         * @see affichageText(txt, delaiChar, delaiFin, retourALaLigne)
         */
        affichageText(txt, 10, delaiFin, retourALaLigne);
    }

    void affichageText(String txt, int delaiChar, int delaiFin, boolean retourALaLigne) {
        /**
         * Affiche le texte mit en paramètre avec un délai à la fin de l'affichage définit en paramètre et avec la possibilité de choisir un retour à la ligne ou non
         * 
         * Il s'agit d'une surcharge de la fonction afficherText(String)
         * 
         * @param txt : Texte à afficher
         * @param delai : Délai à la fin de l'affichage
         * @param retourALaLigne : Définit s'il y aura un retour à la ligne à la fin de l'affichage
         */
        for(int indice = 0; indice < length(txt); indice++) {
            print(charAt(txt, indice));
            delay(delaiChar);
        }
        delay(delaiFin);
        if(retourALaLigne) {
            println("");
        }
    }

    void affichageDialogue(String txt) {
        /**
         * Fonction propre au mode histoire, elle permet l'affichage du texte avec une mise en page personnalisée de certains éléments
         * 
         * Les chaînes commençant par un '–' seront affichées en grise et en italique
         * Chaque itération de "Fey la fée" à n'importe quel endroit du texte sera affiché en violet
         * Chaque itération de "[user]" à n'importe quel endroit du texte sera remplacé par le nom du joueur et affiché en vert
         * 
         * Cette fonction reprend les principes de la fonction affichageText(String)
         * 
         * @param txt : Texte à afficher
         */
        int indice = 0;
        while(indice < length(txt)) {
            if(indice < length(txt) - 10 && equals(substring(txt, indice, indice + 10), "Fey la fée")) {
                affichageText(FEY_LA_FEE, 0, false);
                indice = indice + 10;
            } else if (indice == 0 && charAt(txt, 0) == '–') {
                print(ANSI_ITALIC + ANSI_GREY);
            } else if(indice < length(txt) - 6 && equals(substring(txt, indice, indice + 6), "[user]")) {
                affichageText(ANSI_GREEN + actualPlayer.username + ANSI_RESET, 0, false);
                indice = indice + 6;
            } else if(indice == 0 && charAt(txt, 0) == '_') {
                delay(3000);
                clearScreen();
                return;
            }
            print(charAt(txt, indice));
            delay(50);
            indice++;
        }
        delay(1000);
        println(ANSI_RESET);
    }

    void affichageInput(Question q, int nbQuestion) {
        /**
         * Affichage d'une question de type Input
         * 
         * L'affichage comprendra le numéro de la question ainsi que l'intitulé de la question
         * Cette fonction est dédiée au mode Entraînement
         * 
         * @param q : Question à afficher
         * @param nbQuestion : Numéro de la question
         */
        affichageText("Question " + nbQuestion + " : " + q.question);
    }

    void affichageInput(Question q) {
        /**
         * Affichage d'une question de type Input
         * 
         * L'affichage comprendra uniquement l'intitulé de la question et sera affiché au format dialogue
         * Cette fonction est dédiée au mode Histoire
         * 
         * @param q : Question à afficher
         */
        affichageDialogue(q.question);
    }

    void affichageQCM(Question q, int nbQuestion) {
        /**
         * Affichage d'une question de type QCM
         * 
         * L'affichage comprendra le numéro de la question, son intitulé ainsi que les 4 choix possibles de réponse
         * 
         * @param q : Question à afficher
         * @ param nbQuestion : Numéro de la question
         */
        affichageText("Question " + nbQuestion + " : " + q.question + "\n1. " + q.choix1 + "\n2. " + q.choix2 + "\n3. " + q.choix3 + "\n4. " + q.choix4);
    }

    void affichageVictory() {
        /**
         * Affichage d'un message félicitant pour la victoire
         * Afin de clarifier le code, ce message est stocké dans un fichier .txt
         */
        File f = newFile(VICTORY_PATH);
        while(ready(f)) {
            println(readLine(f));
        }
        delay(3000);
        clearScreen();
    }

    void affichageDefeat() {
        /**
         * Affichage d'un message informant de l'échec de la personne
         * Afin de clarifier le code, ce message est stocké dans un fichier .txt
         */
        File f = newFile(DEFEAT_PATH);
        while(ready(f)) {
            println(readLine(f));
        }
        delay(3000);
        clearScreen();
    }
    
    // Sauvegarde des fichiers CSV ---------------------------------------------------------------------------------------

    void savePlayerCSV() {
        /**
         * Transforme le tableau de Player playerList en un tableau 2D de String puis sauvegarde dans le fichier CSV players.csv
         * Le fichier players.csv se situe dans le dossier files
         * 
         * @see saveCSV
         */
        String[][] players = new String[length(playerList)][4];
        for(int indice = 0; indice < length(players); indice++) {
            String[] player = new String[4];
            Player p = playerList[indice];
            player[0] = p.username;
            player[1] = "" + p.trainingCompleted;
            player[2] = "" + p.storyCompleted;
            player[3] = p.mdp;
            players[indice] = player;
        }
        saveCSV(players, PLAYERS_PATH);
        affichageText("[SAVE] Sauvegarde terminée !", 0);
    }

    void addAndSavePlayer(Player p) {
        /**
         * Ajoute le joueur au tableau de Player global et lance une sauvegarde de ce dernier
         * 
         * @param p : Joueur à ajouter
         */
        Player[] newList = new Player[length(playerList) + 1];
        for(int indice = 0; indice < length(playerList); indice++) {
            newList[indice] = playerList[indice];
        }
        newList[length(playerList)] = p;
        playerList = newList;
        savePlayerCSV();
    }

    void saveQuestionCSV() {
        /**
         * Sauvegarde les questions de la questionList dans le fichier questions.csv
         * Utilisé principalement dans la création de question personnalisées
         */
        String[][] questions = new String[length(questionList)][7];
        for(int indice = 0; indice < length(questions); indice++) {
            Question q = questionList[indice];
            questions[indice][0] = questionTypeToString(q.type);
            questions[indice][1] = q.question;
            if(q.type == QuestionType.INPUT) {
                questions[indice][2] = q.answerInput;
            } else {
                questions[indice][2] = "" + q.answerQCM;
                questions[indice][3] = "" + q.choix1;
                questions[indice][4] = "" + q.choix2;
                questions[indice][5] = "" + q.choix3;
                questions[indice][6] = "" + q.choix4;
            }
        }
        saveCSV(questions, QUESTIONS_PATH);
        affichageText("[SAVE] Questions sauvegardées !", 0);
    }

    void addQuestionToList(Question q) {
        /**
         * Ajoute la question q à la variable globale questionList.
         * Cette fonction sauvegarde automatiquement la nouvelle liste dans le fichier questions.csv
         * 
         * @param q : Question à ajouter
         */
        Question[] newQuestionList = new Question[length(questionList) + 1];
        for(int indice = 0; indice < length(questionList); indice++) {
            newQuestionList[indice] = questionList[indice];
        }
        newQuestionList[length(questionList)] = q;
        questionList = newQuestionList;
        saveQuestionCSV();
    }

    void saveCustomLevels() {
        /**
         * Sauvegarde des niveaux personnalisés du tableau customLevels dans le fichier customLevels.csv
         * Utilisé pour la création de niveaux personnalisés
         */
        String[][] f;
        if(length(customLevels) == 0) { return;}
        else { f = new String[length(customLevels, 1)][length(customLevels, 2)];}
        for(int ligne = 0; ligne < length(f, 1); ligne++) {
            for(int col = 0; col < length(f, 2); col++) {
                f[ligne][col] = "" + customLevels[ligne][col];
            }
        }
        saveCSV(f, CUSTOM_LEVELS_PATH);
        affichageText("[SAVE] Niveaux customs sauvegardés", 0);
    }

    void addCustomLevel(int[] level) {
        /**
         * Ajout d'un niveau dans la liste customLevels et sauvegarde dans le fichier .csv
         * Utilisé dans la création de niveaux personnalisés
         * 
         * @param level : Liste des indices des questions du niveau
         */
        int[][] newList;
        if(length(customLevels) == 0) { newList = new int[1][length(level)];}
        else { newList = new int[length(customLevels) + 1][length(customLevels, 2)];}
        for(int lig = 0; lig < length(customLevels, 1); lig++) {
            newList[lig] = customLevels[lig];
        }
        newList[length(customLevels)] = level;
        customLevels = newList;
        saveCustomLevels();
    }

    // Fonctions diverses ---------------------------------------------------------------------------------------

    boolean isYes(String txt) {
        /**
         * Fonction vérifiant si le texte correspond à la chaîne "o"
         * 
         * Cette fonction est utilisée pour demander une validation à l'utilisateur. On la retrouve notamment dans la demande de création de compte au menu ce connexion
         * 
         * @param txt : Texte saisi par l'utilisateur
         * @return Egalité du texte à la chaîne "o"
         */
        return equals(txt, "o"); 
    }

    void testIsYes() {
        assertTrue(isYes("o"));
        assertFalse(isYes("n"));
        assertFalse(isYes("p"));
    }

    Player newPlayer(String nom, String mdp) {
        /**
         * Renvoie un nouveau joueur de type Player avec le username et le mot de passe correspondant aux paramètres
         * 
         * Fonction créant un nouveau joueur, utilisé à la création de compte.
         * Les valeurs de progression sont initalisées à 0
         * 
         * @param nom : Nom d'utilisateur du joueur
         * @param mdp : Mot de passe crypté du joueur
         * @return Nouveau joueur
         */
        Player p = new Player();
        p.username = nom;
        p.trainingCompleted = 0;
        p.storyCompleted = 0;
        p.mdp = mdp;
        return p;
    }

    void testNewPlayer() {
        Player p = newPlayer("bob", "bobob");
        assertEquals(p.username, "bob");
        assertEquals(p.mdp, "bobob");
    }

    boolean contains(String txt, char carac) {
        /**
         * Vérifie si la chaîne de caractère contient le caractère mit en paramètre
         * 
         * Cette fonction peut être utilisée dans de nombreux contextes.
         * Dans le jeu, elle est utilisée dans le contrôle de saisie d'un nombre entier
         * 
         * @param txt : Texte de référence
         * @param carac : Caractère recherché
         * @return Présence du caractère dans le texte
         * @see saisieNombreEntier(int) pour un exemple d'utilisation
         */
        boolean found = false;
        int indice = 0;
        while(indice < length(txt) && !found) {
            found = charAt(txt, indice) == carac;
            indice++;
        }
        return found;
    }

    void testContains() {
        assertTrue(contains("abc", 'a'));
        assertFalse(contains("", 'a'));
        assertFalse(contains("abc", 'g'));
        assertTrue(contains("0123", '0'));
    }

    boolean playerExists(String name) {
        /**
         * Vérifie l'existance d'un joueur à partir de son username
         * 
         * Permet d'éviter la création d'un nouveau joueur avec un nom d'utilisateur déjà existant
         * 
         * @param name : Nom d'utilisateur recherché
         * @return Existance du joueur dans la base players.csv
         */
        for(int indice = 0; indice < length(playerList); indice++) {
            if(equals(playerList[indice].username, name)) {
                return true;
            }
        }
        return false;
    }

    Player getPlayerByName(String name) {
        /**
         * Renvoie le type player correspondant au joueur de nom name
         * 
         * Cette fonction est notamment utilisée dans la connexion à un joueur déjà existant
         * 
         * @param name : Username de l'utilisateur
         * @return Player correspondant au Username
         * @see connectionMenu() pour un exemple d'utilisation
         */
        for(int indice = 0; indice < length(playerList); indice++) {
            if(equals(playerList[indice].username, name)) {
                return playerList[indice];
            }
        }
        return null;
    }

    int getPlayerIDByName(String name) {
        /**
         * Renvoie la position du player dans le tableau playerList
         * 
         * Si l'utilisateur n'est pas trouvé, la fonction renvoie -1
         * 
         * @param name : Username de l'utilisateur
         * @return Position de l'utilisateur
         * @see deleteAccount() pour un exemple d'utilisation
         */
        for(int indice = 0; indice < length(playerList); indice++) {
            if(equals(playerList[indice].username, name)) {
                return indice;
            }
        }
        return -1;
    }

    String removeComa (String txt) {
        /**
         * Retire toutes les virgules du texte mit en paramètre.
         * Cette fonction est principalement utilisée pour la sauvegarde des questions dans les fichiers CSV pour éviter les conflits.
         * 
         * @param txt : Texte de base
         * @return Texte sans les virgules
         */
        String res = "";
        for (int i = 0; i < length(txt); i++) {
            if (charAt(txt,i) != ',') {
                res = res + charAt(txt,i);
            }
        }
        return res;
    }

    void testRemoveComa() {
        assertEquals("abc", removeComa("a,b,c,"));
        assertEquals("", removeComa(","));
        assertEquals("aB;cD", removeComa("aB,;cD"));
        assertEquals("aabbcc", removeComa("aabbcc"));
    }

    // Gestion mots de passe ---------------------------------------------------------------------------------------

    String cryptage(String txt, char key) {
        /**
         * Crypte le texte en fonction de la clé fournit en paramètre
         * 
         * Utilisation prévue pour le cryptage des mots de passe
         * 
         * @param txt : Texte à encrypter
         * @param key : Clé de cryptage
         * @return Texte crypté
         */
        String mdp = "";
        String username = actualPlayer.username;
        for(int indice = 0; indice < length(txt); indice++) {
            mdp = mdp + (char)(charAt(txt, indice) + key + charAt(username, indice%length(username)));
        }
        return mdp;
    }

    String decryptage(String txt, char key) {
        String mdp = "";
        String username = actualPlayer.username;
        for(int indice = 0; indice < length(txt); indice++) {
            mdp = mdp + (char)(charAt(txt, indice) - key - charAt(username, indice%length(username)));
        }
        return mdp;
    }

    String newMDP() {
        /**
         * Gestion de la création d'un nouveau mot de passe
         * 
         * Contrôle la saisie de l'utilisateur du mot de passe qu'il souhaite définir et de sa confirmation.
         * Fonction récursive s'appelant tant que les deux mots de passe ne correspondent pas
         * 
         * @return Nouveau mot de passe crypté
         */
        String saisie = saisieReponse("Entrez un nouveau mot de passe : ");
        String saisie2 = saisieReponse("Confirmez votre mot de passe : ");
        if (!equals(saisie, saisie2)) {
            println(ANSI_RED + "Les deux mots de passe ne correspondent pas." + ANSI_RESET);
            return newMDP();
        } else if(equals(saisie, "")) {
            println(ANSI_RED + "Le mot de passe ne peut pas être vide." + ANSI_RESET);
            return newMDP();
        } else {
            return cryptage(saisie, '¤');
        }
    }

    boolean checkMDP() {
        /**
         * Gestion de la vérification du mot de passe
         * 
         * Contrôle si le mot de passe entré par l'utilisateur correspond au mot de passe du joueur
         * L'utilisateur peut entrer "retour" pour revenir en arrière. La fonction renverra false
         * Si l'utilisateur rentre correctement son mot de passe, la fonction renverra true
         * 
         * @return Saisie valide du mot de passe
         */
        String saisie = cryptage(saisieReponse("Saisissez votre mot de passe (retour pour quitter) : "), '¤');
        if(equals(cryptage("retour", '¤'), saisie)) {
            return false;
        }
        if(equals(saisie, actualPlayer.mdp)) {
            return true;
        }
        println(ANSI_RED + "Mot de passe incorrect" + ANSI_RESET);
        return checkMDP();
    }

    // Menu connexion ---------------------------------------------------------------------------------------

    void connectionMenu() {
        /**
         * Affichage du menu de connexion et de création de joueur
         * 
         * Gestion de l'entrée du nom d'utilisateur, avec vérification de l'existance d'un joueur de ce nom ou non
         * Vérification de la validité du mot de passe, création d'un nouveau mot de passe si aucun n'est définit
         * Création d'un joueur si aucun n'existe sous le nom entré par l'utilisateur
         * Cette fonction effectue également une série de vérification pour éviter la triche et les bugs de niveaux trop élevés
         */
        boolean valide = false;
        affichageDrapeau();
        affichageText(ANSI_RED + "Bienvenue sur DoubleLangue !" + ANSI_RESET + "\nPour commencer, merci d'entrer votre nom d'utilisateur.\n" + ANSI_BLUE + "Votre progression sera sauvegardée par le biais de ce nom." + ANSI_RESET);
        while(!valide) {
            String nom = removeComa(toUpperCase(saisieReponse(false, "Nom d'utilisateur (exit pour quitter) : ")));
            if (playerExists(nom)) {
                actualPlayer = getPlayerByName(nom);
                affichageText("Utilisateur " + actualPlayer.username + " trouvé.");
                
                // Vérification que les niveaux complétés ne dépassent pas le nombre de niveaux existants 
                valide = checkPlayer();
            } else {
                if (equals(nom, "EXIT")) {
                    affichageText("Fermeture du jeu . . .");
                    savePlayerCSV();
                    println("[SAVE] Joueurs sauvegardés");
                    System.exit(0);
                } else if(equals(nom, "")) {
                    affichageText(ANSI_RED + "Le nom d'utilisateur ne peut pas être vide." + ANSI_RESET);
                } else if(equals(nom, "SHREK")) {
                    addCustomLevel(new int[]{1, 1, 1, 1, 1});
                }
                else {
                    valide = createAccount(nom);
                }
            }
        }
        savePlayerCSV();
        mainMenu();
    }

    boolean checkPlayer() {
        /**
         * Vérifications du joueur actuel concernant sa progression et son mot de passe.
         * Si le joueur n'a pas de mot de passe, il devra en faire un
         * Sinon, il devra entrer son mot de passe.
         * 
         * Si une incohérence est remarquée dans la progression du joueur, un message sera affiché et la progression sera réinitialisée.
         * 
         * @return Joueur et mot de passe valides
         */
        boolean valide = false;
        if (actualPlayer.trainingCompleted > length(trainingLevels)) {
            actualPlayer.trainingCompleted = 0;
            affichageText("Une incohérence a été remarquée dans les niveaux d'entraînements complétés. Nous avons remis tout ça en ordre ne vous en faites pas ;)");
        }
        if (actualPlayer.storyCompleted > length(dialogues)) {
            actualPlayer.storyCompleted = 0;
            affichageText("Une incohérence a été remarquée dans les niveaux histoire complétés. Nous avons remis tout ça en ordre ne vous en faites pas ;)");
        }
        if (equals(actualPlayer.mdp, " ")) { // Connexion au jeu avec un ancien compte (sans mot de passe)
            affichageText("Aucun mot de passe n'a été définit.");
            actualPlayer.mdp = newMDP();
            valide = true;
        } else {
            if(checkMDP()) {
                valide = true;
            }
        }
        return valide;
    }

    boolean createAccount(String nom) {
        /**
         * Demande au joueur s'il veut créer un nouvel utilisateur
         * 
         * @param nom : Nom d'utilisateur du joueur
         * @return Joueur correctement créé
         */
        boolean valide = false;
        affichageText("Aucun utilisateur trouvé.");
        String validation = saisieReponse("Souhaitez-vous créer un nouvel utilisateur du nom de " + nom + " ? (o/n) ");
        if (isYes(validation)) {
            affichageText("Création du compte en cours . . .");
            Player p = newPlayer(nom, " ");
            actualPlayer = p;
            actualPlayer.mdp = newMDP();
            addAndSavePlayer(p);
            affichageText("Compte créé ! Votre progression sera sauvegardée !", 0);
            affichageText("Bienvenue sur DoubleLangue !", 5000);
            valide = true;
        }
        return valide;
    }
    
    // Menu principal ---------------------------------------------------------------------------------------

    void mainMenu() {
        /**
         * Menu principal affichant tous les modes de jeux et les sous-menus pouvant être accessibles
         * 
         * L'utilisateur aura le choix entre :
         * - Voir les règles
         * - Jouer (mode Histoire ou Entraînement)
         * - Modifier les paramètres du compte
         */
        affichageDrapeau();
        affichageText("=========== Bienvenue " + ANSI_GREEN + actualPlayer.username + ANSI_RESET + " ===========", 0);
        affichageText("Choisissez votre mode de jeu :\n0. Déconnexion\n1. Mode histoire (Progression : " + (actualPlayer.storyCompleted * 100 / length(dialogues)) + " %)\n2. Mode entraînement (Progression : " + (actualPlayer.trainingCompleted * 100 / length(trainingLevels)) + " %)\n3. Questions et niveaux personnalisés (" + ANSI_BLINK_SLOW + ANSI_RED + "Nouveau!" + ANSI_RESET + ")\n4. Règles du jeu \n5. Paramètres", 0);
        int option = saisieNombreEntier(5, "Entrez votre choix : ");
        switch (option) {
            case 0:
                affichageText("Votre progression va être sauvegardée. Veuillez patienter . . .");
                savePlayerCSV();
                connectionMenu();
                return;
            case 1:
                clearScreen();
                story(actualPlayer.storyCompleted);
                return;
            case 2:
                trainingModeSelection();
                return;
            case 3:
                customModes();
                return;
            case 4:
                rules();
                return;
            case 5:
                settings();
        }
    }

    void rules() {
        /**
         * Affichage des règles de base du jeu
         * Récupération du fichier rules.txt dans le dossier ressources
         */
        clearScreen();
        affichageText(ANSI_BOLD + "Bienvenue sur " + ANSI_GREEN + "DoubleLangue !" + ANSI_RESET);
        File f = newFile(RULES_PATH);
        while(ready(f)) {
            affichageText(readLine(f), 0);
        }
        affichageText("Appuyez sur " + ANSI_ITALIC + "entrée" + ANSI_RESET + " pour continuer", 0, false);
        readString();
        mainMenu();
    }

    void customModes() {
        /**
         * Sélection du mode personnalisé
         * Dans ce menu, l'utilisateur choisira entre jouer et créer un niveau personnalisé
         */
        clearScreen();
        affichageText(ANSI_GREEN + "========= Niveaux personnalisés =========" + ANSI_RESET, 0);
        affichageText("Sélectionnez le mode :\n0. Retour\n1. Jouer aux niveaux perso\n2. Mode création", 0);
        int saisie = saisieNombreEntier(2, "Votre sélection : ");
        switch(saisie) {
            case 0:
                mainMenu();
                break;
            case 1:
                selectionCustomLevel(0);
                break;
            case 2:
                createCustoms();
                break;
        }
    }

    void createCustoms() {
        /**
         * Menu de sélection d'une question personnalisé ou d'un niveau personnalisé
         * L'utilisateur choisira entre créer une question et un niveau
         */
        affichageText("Que souhaitez-vous créer ?\n0. Rien\n1. Nouvelle question\n2. Nouveau niveau", 0);
        int saisie = saisieNombreEntier(2, "Sélection : ");
        switch(saisie) {
            case 0:
                mainMenu();
                break;
            case 1:
                selectionType();
                break;
            case 2:
                creationLevel();
                break;
        }
    }

    // Gestion des saisies ---------------------------------------------------------------------------------------

    int saisieNombreEntier(int maxOption, String message) {
        /**
         * Contrôle la saisie d'un nombre entier par l'utilisateur entre 0 et le nombre maximum définit en paramètre
         * 
         * La saisie ne peut pas être plus grande que 1 chiffre (le maximum doit donc être inférieur ou égal à 9)
         * Le chiffre maximum en paramètre est inclus dans la vérification et peut être entré par l'utilisateur
         * La fonction peut être utilisée dans les menus par exemple
         * 
         * @param maxOption : Chiffre maximum pouvant être entré par l'utilisateur
         * @param message : Message à afficher avant l'attente de saisie de l'utilisateur
         * @return Entier entré par l'utilisateur
         * @see saisieNombreEntier(minOption, maxOption, message)
         */
        return saisieNombreEntier(0, maxOption, message);
    }

    int saisieNombreEntier(int minOption, int maxOption, String message) {
        /**
         * Contrôle la saisie d'un nombre entier par l'utilisateur entre le nombre minimum et le nombre maximum définits en paramètre
         * 
         * La fonction fonctionne de la même manière que saisieNombreEntier(int maxOption)
         * Les bornes sont incluses ( nombres acceptés : [minOption, maxOption] )
         * 
         * @param minOption : Chiffre minimum pouvant être entré par l'utilisateur
         * @param maxOption : Chiffre maximum pouvant être entré par l'utilisateur
         * @param message : Message à afficher avant l'attente de saisie de l'utilisateur
         * @return Entier entré par l'utilisateur
         */
        boolean valide = false;
        String saisie = "";
        while(!valide) {
            affichageText(message, 0, false);
            saisie = readString();
            if(length(saisie) == 1 && contains("0123456789", charAt(saisie, 0)) && stringToInt(saisie) <= maxOption && stringToInt(saisie) >= minOption) {
                valide = true;
            } else {
                println("Saisie incorrecte.");
            }
        }
        return stringToInt(saisie);
    }

    String removeSpecials(String txt) {
        /**
         * Fonction récursive renvoyant la chaîne en paramètre sans les caractères spéciaux
         * 
         * @param txt : Chaîne de départ
         * @return Chaîne sans caractère spécial
         */
        if(length(txt) == 0) {
            return "";
        }
        char carac = charAt(txt, 0);
        if(carac >= 'a' && carac <= 'z' || carac >= 'A' && carac <= 'Z') {
            return "" + carac + removeSpecials(substring(txt, 1, length(txt)));
        }
        return "" + removeSpecials(substring(txt, 1, length(txt)));
    }

    boolean isGoodQCMAnswer(Question q, int a) {
        /**
         * Vérifie si la réponse du joueur correspond à la réponse attendue de la question QCM
         * 
         * Cette fonciton ajoute 1 à la réponse, prenant en compte le décalage d'indice
         * 
         * @param q : Question correspondante
         * @param a : Réponse de l'utlisateur
         * @return Correspodance de la réponse à la question et de celle entrée par l'utilisateur
         */
        return q.answerQCM + 1 == a;
    }

    void testIsGoodQCMAnswer() {
        Question q = new Question();
        q.answerQCM = 2;
        assertTrue(isGoodQCMAnswer(q, 3));
    }

    String saisieReponse(String message) {
        /**
         * Récupère et renvoie l'entrée d'un utilisateur
         * 
         * Aucune vérification n'est effectuée par la fonction
         * 
         * @return Entrée utilisateur
         */
        return saisieReponse(true, message);
    }

    String saisieReponse(boolean canBeEmpty, String message) {
        /**
         * Récupère et renvoie l'entrée d'un utilisateur
         * 
         * Si le paramètre est sur true, la fonction n'acceptera pas les entrées vides
         * 
         * @param canBeEmpty : Entrée vide de l'utilisateur valide ou non
         * @param message : Message à afficher avant l'attente de saisie de l'utilisateur
         * @return Entrée utilisateur
         * @see saisieReponse()
         */
        affichageText(message, 0, false);
        String saisie = readString();
        if(!canBeEmpty && equals(saisie, "")) {
            println(ANSI_RED + "L'entrée ne peut pas être vide. Réessayez" + ANSI_RESET);
            return saisieReponse(canBeEmpty, message);
        }
        return saisie;
    } 

    // Mode entraînement ---------------------------------------------------------------------------------------

    void trainingModeSelection() {
        /**
         * Affichage des différentes possibilités d'entraînement en fonction de l'avancée du joueur
         * 
         * Si le joueur joue pour la première fois au mode de jeu (ou n'a toujours pas terminé le 1er niveau), le niveau 1 se lance automatiquement
         * Si le joueur a fini tous les niveaux du module d'entraînement, la possibilité de jouer le niveau suivant est caché
         * Sinon, le joueur peut soit revenir en arrière, soit rejouer un niveau, soit joueur le niveau suivant
         */
        clearScreen();
        if(actualPlayer.trainingCompleted == 0) {
            affichageText("===== Bienvenue dans le module d'entraînement ! =====", 0);
            affichageText("Ici, vous trouverez des niveaux pour vous entraîner au mode histoire.", 0);
            affichageText("Commençons par le premier niveau !", 0);
            playTraining(0);
        } else {
            affichageText("============ Mode Entraînement ============", 0);
            affichageText("Lors de votre dernière session, vous êtes arrivés au niveau " + actualPlayer.trainingCompleted + " sur les " + length(trainingLevels) + " disponibles.");
            int max;
            if(actualPlayer.trainingCompleted >= length(trainingLevels)) {
                affichageText("0. Retour\n1. Rejouer un niveau", 0);
                max = 1;
            } else {
                affichageText("0. Retour\n1. Rejouer un niveau\n2. Continuer vers le niveau " + (actualPlayer.trainingCompleted + 1), 0);
                max = 2;
            }
            int saisie = saisieNombreEntier(max, "Entrez votre sélection : ");
            switch(saisie) {
                case 0:
                    savePlayerCSV();
                    mainMenu();
                    return;
                case 1:
                    trainingSelection(0);
                    return;
                case 2:
                    playTraining(actualPlayer.trainingCompleted);
                    return;
            }
        }
    }

    void trainingSelection(int page) { 
        /**
         * Affichage de l'ensemble des niveaux terminés par l'utilisateur, permettant de le rejouer
         * 
         * Pour palier à la possibilité qu'il y ait plus de 8 niveaux, un système de page est mit en place
         * Les niveaux affichés seront les niveaux correspondant à 8 fois la page
         * L'utilisateur pourra ensuite sélectionner le niveau qu'il souhaite rejouer et voyager entre les pages
         * 
         * @param page : Numéro de la page
         */
        clearScreen();
        affichageText("Vous pouvez rejouer un niveau que vous avez déjà terminé. Choisissez un niveau : ", 0);
        String choices;
        println("======= Page ("+ (page + 1) + "/" + (length(trainingLevels) / 8 + 1) + ") =======");
        if (page == 0) {choices = "0. Retour\n";}
        else {choices = "0. Page precedente\n";}

        int limite;

        if (actualPlayer.trainingCompleted - (page * 8) > 8) {limite = 8;}
        else {limite = actualPlayer.trainingCompleted - (page * 8); }

        int indice = 1;

        while(indice <= limite) {
            choices += "" + indice + ". Niveau " + (indice + (page * 8)) + "\n";
            indice++;
        }

        if (limite == 8) {
            choices += "9. Page suivante\n";
            limite++;
        }
        choices += "==========================";
        println(choices);

        int saisie = saisieNombreEntier(limite, "Saisissez votre choix : ");
        switch(saisie) {
            case 0:
                if(page == 0) {trainingModeSelection();}
                else {trainingSelection(page-1); }
                return;
            case 9:
                trainingSelection(page+1);
                return;
            default:
                playTraining(saisie + 8 * page - 1);
                return;
        }
    }

    int play(Question[] niveau, int tentatives) {
        /**
         * Gestion de la saisie des réponses de l'utilisateur, du nombre de tentatives du joueur et de l'affichage des questions
         * 
         * La fonction revoie le nombre de tentatives restantes, permettant l'action en conséquence, et fonctionne tant qu'il reste des tentatives à l'utilisateur et qu'il n'a pas terminé l'ensemble des questions
         * Principalement utilisée dans le mode Entraînement
         * 
         * @param niveau : Liste des questions à jouer
         */
        int question = 0;

        while (question < length(niveau) && tentatives > 0) {
            if(niveau[question].type == QuestionType.QCM) {
                affichageQCM(niveau[question], question + 1);
                while(tentatives > 0 && !isGoodQCMAnswer(niveau[question], saisieNombreEntier(4, "Votre réponse : "))) {
                    tentatives -= 1;
                    affichageText(ANSI_RED + "Mauvaise réponse. Il vous reste " + tentatives + " tentative(s)" + ANSI_RESET);
                }
            } else {
                affichageInput(niveau[question], question + 1);
                String res = removeSpecials(toUpperCase(niveau[question].answerInput));
                String uRes = removeSpecials(toUpperCase(saisieReponse(false, "Votre réponse : ")));
                while(tentatives > 0 && !equals(res, uRes)) {
                    tentatives -= 1;
                    affichageText(ANSI_RED + "Mauvaise réponse. Il vous reste " + tentatives + " tentative(s)" + ANSI_RESET);
                    uRes = removeSpecials(toUpperCase(saisieReponse(false, "Votre réponse : ")));
                }
            }
            if (tentatives > 0) {
                affichageText(ANSI_GREEN + "Bonne réponse !" + ANSI_RESET);
                question++;
            }
        }
        if(tentatives == 0) {
            Question q = niveau[question];
            String answer = "";
            if(q.type == QuestionType.QCM) {
                switch(q.answerQCM) {
                    case 0:
                        answer = q.choix1;
                    case 1:
                        answer = q.choix2;
                    case 2:
                        answer = q.choix3;
                    case 3:
                        answer = q.choix4;
                }
            } else {
                answer = q.answerInput;
            }
            affichageText(ANSI_RED + ANSI_BOLD + "La bonne réponse était " + answer + ". Retente ta chance !" + ANSI_RESET);
        }
        return tentatives;
    }

    void playTraining(int level) { 
        /**
         * Lance le niveau d'entraînement et vérifie la victoire ou non du joueur
         * Les valeurs de progression du joueur dans le mode sont modifiées ici
         * 
         * @param level : Indice du niveau à jouer correspondant à la position dans trainingLevels
         */
        Question[] niveau = new Question[length(trainingLevels, 2)];
        int tentatives = 5;

        // Récupération des questions
        for(int indice = 0; indice < length(niveau); indice++) {
            niveau[indice] = questionList[trainingLevels[level][indice]];
        }

        // Lancement des questions
        tentatives = play(niveau, tentatives);

        // Vérification de la victoire ou de la défaite du joueur
        if (tentatives > 0) {
            affichageVictory();
            if (actualPlayer.trainingCompleted == level) { // Affichage si c'est la première fois que le niveau est gégné
                actualPlayer.trainingCompleted++;
                affichageText("Congrats ! Vous avez complètement terminé ce niveau ! Vous pouvez désormais jouer au niveau " + (actualPlayer.trainingCompleted + 1));
            } else { // Affichage si la personne avait déjà gagné le niveau précédemment
                affichageText("Congrats ! Vous avez complètement terminé ce niveau ! Vous n'avez pas perdu la main ;)");
            }
            // Sauvegarde du joueur
            savePlayerCSV();
            trainingModeSelection();
        } else {
            affichageDefeat();
            affichageText("Dommage... Vous n'avez pas terminé ce niveau. Vous pouvez toujours réessayer");
            mainMenu();
        }
    }

    // Jouer niveaux customs ---------------------------------------------------------------------------------------

    void playCustom(int[] questions, int tentatives) {
        /**
         * Lance et joue les questions d'un niveau personnalisé
         * La fonction transforme la liste d'indice questions en une liste de question pour ensuite utiliser la fonction play.
         * 
         * @param questions : Liste des indices des questions
         * @param tentatives : Nombre de tentatives
         * @see play(Question[], int)
         */
        Question[] niveau = new Question[length(questions)];
        for(int indice = 0; indice < length(questions); indice++) {
            niveau[indice] = questionList[questions[indice]];
        }
        tentatives = play(niveau, tentatives);
        if(tentatives > 0) {
            affichageVictory();
            selectionCustomLevel(0);
        } else {
            affichageDefeat();
            selectionCustomLevel(0);
        }
    }

    void selectionCustomLevel(int page) {
        /**
         * Sélection et lancement d'un niveau personnalisé
         * Pour un affichage optimisé et clair, l'affichage se fait en pages
         */
        clearScreen();
        String choices;
        int tentatives = 5;
        if(length(customLevels) == 0) {
            affichageText(ANSI_RED + "Aucun niveau personnalisé n'a été créé. Rendez-vous dans le mode création pour créer les vôtres !");
            return;
        }
        affichageText("======= Page ("+ (page + 1) + "/" + (length(customLevels) / 8 + 1) + ") =======", 0);
        if (page == 0) {choices = "0. Retour\n";}
        else {choices = "0. Page precedente\n";}

        int limite;

        if (length(customLevels) - (page * 8) > 8) {limite = 8;}
        else {limite = length(customLevels) - (page * 8); }

        int indice = 1;

        while(indice <= limite) {
            choices += "" + indice + ". Niveau " + (indice + (page * 8)) + "\n";
            indice++;
        }

        if (limite == 8) {
            choices += "9. Page suivante\n";
            limite++;
        }
        choices += "==========================";
        affichageText(choices, 0);

        int saisie = saisieNombreEntier(limite, "Saisissez votre choix : ");
        switch(saisie) {
            case 0:
                if(page == 0) {customModes();}
                else {selectionCustomLevel(page-1); }
                return;
            case 9:
                selectionCustomLevel(page+1);
                return;
            default:
                playCustom(customLevels[saisie - 1], tentatives);
                break;
            
        }
    }

    // Mode Histoire ---------------------------------------------------------------------------------------

    boolean playQuestion(Question q, int tentatives) {
        /**
         * Joue la question en paramètre avec un affichage type dialogue
         * 
         * Les messages de mauvaises réponses sont propres au mode histoire
         * Le nombre de tentatives est unique à chaque question dans ce mode
         * 
         * @param q : Question correspondante
         * @param tentatives : Nombre de tentatives pour le niveau
         * @return Réussite du niveau
         */
        String[] messages = new String[]{FEY_LA_FEE + " : \"C'est bien ce que je pensais, tu n'es pas à la hauteur... Reviens quand tu seras prêt.\"", FEY_LA_FEE + " : \"Attention, si tu ne réponds pas correctement, je ne vais pas pouvoir te laisser continuer\"", FEY_LA_FEE + " : \"Oh oh, on dirait que ce n’est pas la bonne réponse, réessaie !\""};
        affichageInput(q);
        while(tentatives > 0) {
            String res = removeSpecials(toUpperCase(q.answerInput));
            String uRes = removeSpecials(toUpperCase(saisieReponse(false, "Votre réponse : ")));
            if(equals(res, uRes)) {
                return true;
            }
            tentatives = tentatives - 1;
            affichageText(messages[tentatives]);
        }
        affichageText(".", 1000, false);
        affichageText(".", 1000, false);
        affichageText(".", 1000, false);
        return false;
    }

    void playStory(int completion, int tentatives) {
        /**
         * Gestion de l'affichage des dialogues du mode histoire, des lancements des questions et de la sauvegarde de l'avancée du joueur
         * 
         * Les dialogues sont stockés sous forme de tableau de chaîne de caractère, divisé en section.
         * Les sections sont marquées par les ... dans le fichier dialogue.txt
         * Certaines sections sont plus ou moins grandes que les autres, ainsi, certaines cases null peuvent exister à la fin des lignes.
         * Pour palier à ce problème, une vérification de l'existance de la chaine est effectuée à chaque itération
         * 
         * @param completion : Avancée du joueur et section à jouer
         * @param tentatives : Nombre de tentatives du joueur pour chaque question
         */
        String[] dialogue = dialogues[completion];
        int indice = 0;
        boolean success = true;
        while(indice < length(dialogue) && success) {
            String actualText = dialogue[indice];
            if(actualText == null) {
                indice = length(dialogue);
            } else if (equals(actualText, "")) {
                indice++;
                actualText = dialogue[indice];
                Question question = questionList[stringToInt(actualText)];
                success = playQuestion(question, tentatives);   
            } else {
                affichageDialogue(actualText);
            }
            indice++;
        }
        if(success) {
            actualPlayer.storyCompleted++;
            endOfTurn();
        } else {
            mainMenu();
        }
        return;
    }

    void endOfTurn() {
        /**
         * Fonction jouée à la fin de chaque tour du mode Histoire, elle propose au joueur de faire une pause sur l'aventure
         * Si le joueur a atteint la fin de l'histoire, la fonction affichera une pierre tombale, puis des retour à la ligne toutes les 200 ms pour faire dispataître petit à petit la pierre tombale
         * 
         * @see affichagePierre()
         */
        if(actualPlayer.storyCompleted == 4) {
            affichagePierreTombale();
            delay(3000);
            for(int indice = 0; indice < 50; indice++) {
                println("");
                delay(200);
            }
        }
        affichageText(".", 500, false);
        affichageText(".", 500, false);
        affichageText(".", 500);
        savePlayerCSV();
        affichageText("Vous venez de terminer la partie " + actualPlayer.storyCompleted + " du mode histoire.",0, false);
        if(isYes(saisieReponse("Souhaitez-vous continuer ? (o/n)"))) {
            clearScreen();
            story(actualPlayer.storyCompleted);
        }
        else {mainMenu();}
        return;
    }

    void story(int level) {
        /**
         * Vérification de la progression du joueur et lancement du mode histoire
         * 
         * Si le joueur atteint la fin du jeu (level correspond au nombre de sections total), la fonction proposera de recommencer l'histoire en supprimant la progression.
         * Sinon, la fonction lancera le niveau correspondant
         * 
         * @param level : Avancée du joueur et niveau à lancer
         */
        if (length(dialogues) == level) {
            affichageText("Vous avez terminé le mode histoire ! Félicitations !", 0, false);
            if(isYes(saisieReponse("Souhaitez-vous recommencer ? Attention ! Votre progression sera écrasée ! (o/n)"))) {
                actualPlayer.storyCompleted = 0;
                affichageText("Progression écrasée. See you soon et bonne chance dans votre quête !");
            } else {
                affichageText("Vous pourrez recommencer l'histoire quand vous voulez en revenant ici ! See you soon !");
            }
            savePlayerCSV();
            mainMenu();
            return;
        }

        playStory(level, 3);
    }

    // Paramètrage ---------------------------------------------------------------------------------------

    void settings() {
        /**
         * Menu affichant les différents paramètres existants et redirigeant vers les menus associés
         * 
         * Liste des paramètres :
         * - Modifier le pseudo
         * - Nouveau mot de passe
         * - Réinitialisation de la progression
         * - Suppression du compte
         */
        clearScreen();
        affichageText("=========== Paramètres du jeu ============", 0);
        affichageText("Choisissez un paramètre :\n0. Retour\n1. Modifier votre pseudo\n2. Réinitialiser le profil\n3. Définir un nouveau mot de passe\n4. Supprimer votre compte", 0);
        int entree = saisieNombreEntier(4, "Saisissez votre choix : ");
        switch(entree) {
            case 0:
                mainMenu();
                return;
            case 1:
                editUsername();
                return;
            case 2:
                doubleCheckReset();
                return;
            case 3:
                if(checkMDP()) {
                    actualPlayer.mdp = newMDP();
                    affichageText("Mot de passe modifié.");
                }
                settings();
                return;
            case 4:
                deleteAccount();
        }
    }

    void deleteAccount() {
        /**
         * ! Dangerous Zone !
         * Fonction de suppression du joueur de la base de données
         * Cette fonction effectue 2 vérifications supplémentaires sur la volonté de l'utilisateur de supprimer son compte et demande le mot de passe
         */
        affichageText(ANSI_BOLD + ANSI_RED + "Attention ! Cette action est irréversible ! ", 0, false);
        if(isYes(saisieReponse("Êtes vous sûrs de vouloir supprimer votre compte ? (o/n) " + ANSI_RESET))) {
            affichageText("Dernière chance de revenir en arrière ! ", 0, false);
            if(isYes(saisieReponse("Voulez-vous vraiment supprimer votre compte ? (o/n) "))) {
                if(checkMDP()) {
                    affichageText(ANSI_RED + "Suppresion en cours du compte . . ." + ANSI_RESET, 2000);
                    Player[] newPlayerList = new Player[length(playerList) - 1];
                    int indiceNewList = 0;
                    int indicePlayer = getPlayerIDByName(actualPlayer.username);
                    for(int indice = 0; indice < length(playerList); indice++) {
                        if(indice != indicePlayer) {
                            newPlayerList[indiceNewList] = playerList[indice];
                            indiceNewList++;
                        }
                    }
                    playerList = new Player[length(newPlayerList)];
                    for(int indice = 0; indice < length(playerList); indice++) {
                        playerList[indice] = newPlayerList[indice];
                    }
                    savePlayerCSV();
                    affichageText("Compte correctement supprimé. A très vite");
                    connectionMenu();
                    return;
                }
            }
        }
        settings();
        return;
    }

    void editUsername() {
        /**
         * Modification du nom d'utilisateur du joueur
         * La fonction vérifie si le nom d'utilisateur n'est pas déjà utilisé
         */
        String newUsername = toUpperCase(saisieReponse("Entrez votre nouveau nom d'utilisateur (entrez annuler pour revenir en arrière) : "));
        if (equals(newUsername, "annuler")) {
            affichageText("Annulation . . .");
            settings();
        } else if(playerExists(newUsername)) {
            affichageText("Nom d'utilisateur déjà utilisé.");
            editUsername();
        } else {
            if(isYes(saisieReponse("Êtes vous sur de vouloir modifier votre nom d'utilisateur (Ancien : " + actualPlayer.username + " / Nouveau : " + newUsername + ") ? (o/n)"))) {
                String mdp = decryptage(actualPlayer.mdp, '¤');
                actualPlayer.username = newUsername;
                actualPlayer.mdp = cryptage(mdp, '¤');
                affichageText("Votre nom d'utilisateur a été mis à jour !");
            } else {
                affichageText("Annulation . . .");
            }
            settings();
        }
    }

    void doubleCheckReset() {
        /**
         * ! Dangerous Zone !
         * Fonction de réinitialisation de la progression du joueur
         * Cette fonction effectue 2 vérifications supplémentaires sur la volonté de l'utilisateur de supprimer sa progression et demande le mot de passe
         */
        if(isYes(saisieReponse("Êtes-vous sur de vouloir réinitialiser votre profil ? Cette action est irréversible. (o/n)"))) {
            if(isYes(saisieReponse("Dernière chance d'annuler la réinitialisation. Souhaitez-vous vraiment tout supprimer ? (o/n)"))) {
                if(checkMDP()) {
                    actualPlayer = newPlayer(actualPlayer.username, actualPlayer.mdp);
                    savePlayerCSV();
                    affichageText("Progression réinitialisée.", 2000);   
                }
            }
        }
        settings();
    }

    // Création de questions personnalisées ---------------------------------------------------------------------------------------

    Question newQuestion(QuestionType type) {
        /**
         * Création d'une nouvelle question de type type.
         * 
         * Renvoie la question avec le type initialisé
         * 
         * @param type : Type de question
         * @return Nouvel objet de type question
         */
        Question q = new Question();
        q.type = type;
        return q;
    }

    void testNewquestion() {
        assertEquals(QuestionType.INPUT, newQuestion(QuestionType.INPUT).type);
        assertEquals(QuestionType.QCM, newQuestion(QuestionType.QCM).type);
    }

    void selectionType() {
        /**
         * Selection du type de question que le joueur souhaite créer
         * 
         * La question fait appel à la fonction creationQCM() ou creationINPUT() en fonction du type 
         * 
         * @see saveQuestionCSV()
         */
        affichageText("Choisissez le type de question que vous souhaitez créer parmi :", 0);
        affichageText("1. QCM", 0);
        affichageText("2. INPUT", 0);
        int saisie = saisieNombreEntier(1,2, "Votre choix : ");
        if(saisie == 1) {
            creationQCM();
        } else {
            creationINPUT();
        }
    }

    void creationQCM() {
        /**
         * Menu de création d'une question de type QCM
         * La personne peut personnaliser sa question et les différentes réponses.
         * A la fin de la saisie et après validation, la question est ajoutée à la liste des questions et sauvegardée dans le fichier questions.csv
         */
        Question q = newQuestion(QuestionType.QCM);
        boolean valide = false;
        q.question = saisieReponse(false, "Quel est l'intitulé de la question (exemple : Quelle est la traduction de 'blue') : ");
        affichageText("Bien. Maintenant, vous allez saisir les 4 choix possible par l'utilisateur.", 0);
        q.choix1 = saisieReponse(false, "Entrez la réponse 1 : ");
        q.choix2 = saisieReponse(false, "Entrez la réponse 2 : ");
        q.choix3 = saisieReponse(false, "Entrez la réponse 3 : ");
        q.choix4 = saisieReponse(false, "Entrez la réponse 4 : ");
        affichageText("Parmi ces réponses, laquelle est la bonne ? \n0. " + q.choix1 +  "\n1. " + q.choix2 +"\n2. " + q.choix3 + "\n3. " + q.choix4, 0);
        q.answerQCM = saisieNombreEntier(3, "Bonne réponse : ");
        affichageText("Voyons en revue tout ce que vous avez entré : ");
        while(!valide) {
            clearScreen();
            println("===== Question " + length(questionList) + " =====");
            println("Type : " + questionTypeToString(q.type));
            println("0. Retour sans sauvegarder");
            println("1. Intitulé de la question (" + q.question + ")");
            println("2. Réponse 1 (" + q.choix1 + ")");
            println("3. Réponse 2 (" + q.choix2 + ")");
            println("4. Réponse 3 (" + q.choix3 + ")");
            println("5. Réponse 4 (" + q.choix4 + ")");
            println("6. Bonne réponse (Réponse " + (q.answerQCM + 1) + ")");
            println("7. Valider");
            println("=======================");
            int saisie = saisieNombreEntier(7, "Entrez votre choix : ");
            switch(saisie) {
                case 0:
                    mainMenu();
                    return;
                case 1:
                    q.question = saisieReponse(false, "Quel est l'intitulé de la question (exemple : Quelle est la traduction de 'blue') : ");
                    break;
                case 2:
                    q.choix1 = saisieReponse(false, "Entrez la réponse 1 : ");
                    break;
                case 3:
                    q.choix2 = saisieReponse(false, "Entrez la réponse 2 : ");
                    break;
                case 4:
                    q.choix3 = saisieReponse(false, "Entrez la réponse 3 : ");
                    break;
                case 5:
                    q.choix4 = saisieReponse(false, "Entrez la réponse 4 : ");
                    break;
                case 6:
                    affichageText("Parmi ces réponses, laquelle est la bonne ? \n0. " + q.choix1 +  "\n1. " + q.choix2 +"\n2. " + q.choix3 + "\n3. " + q.choix4, 0);
                    q.answerQCM = saisieNombreEntier(3, "Bonne réponse : ");
                    break;
                case 7:
                    valide = true;
                
            }
        }
        q.question = removeComa(q.question);
        q.choix1 = removeComa(q.choix1);
        q.choix2 = removeComa(q.choix2);
        q.choix3 = removeComa(q.choix3);
        q.choix4 = removeComa(q.choix4);
        addQuestionToList(q);
        customModes();
    }

    void creationINPUT() {
        /**
         * Menu de création d'une question de type INPUT
         * La personne peut personnaliser sa question et la réponse
         * A la fin de la saisie et après validation, la question est ajoutée à la liste des questions et sauvegardée dans le fichier questions.csv
         */
        Question q = newQuestion(QuestionType.INPUT);
        boolean valide = false;
        q.question = saisieReponse(false, "Quel est l'intitulé de la question (exemple : Quelle est la traduction de 'blue') : ");
        q.answerInput = saisieReponse(false, "Quelle est la réponse à la question ? ");
        affichageText("Bien. Passons en revue votre saisie : ");
        while(!valide) {
            clearScreen();
            println("===== Question " + length(questionList) + " =====");
            println("Type : " + questionTypeToString(q.type));
            println("0. Retour sans sauvegarder");
            println("1. Intitulé de la question (" + q.question + ")");
            println("2. Réponse (" + q.answerInput + ")");
            println("3. Valider");
            println("=======================");
            int saisie = saisieNombreEntier(3, "Entrez votre choix : ");
            switch(saisie) {
                case 0:
                    mainMenu();
                    return;
                case 1:
                    q.question = saisieReponse(false, "Quel est l'intitulé de la question (exemple : Quelle est la traduction de 'blue') : ");
                    break;
                case 2:
                    q.answerInput = saisieReponse(false, "Quelle est la réponse à la question ? ");
                    break;
                case 3:
                    valide = true;
            }
        }
        q.question = removeComa(q.question);
        q.answerInput = removeComa(q.answerInput);
        addQuestionToList(q);
        affichageText("Question correctement créée ! Utilisez la dans les niveaux personnalisés (Question " + (length(questionList)- 1) + ").", 3000);
        customModes();
    }

    // Création de niveaux personnalisés ---------------------------------------------------------------------------------------

    int selecLevel(int page) {
        /**
         * Menu de sélection d'une question pour l'ajouter à la liste d'un niveau
         * L'affichage se fait par page pour afficher le maximum de questions tout en restant clair
         * 
         * @param page : Numéro de page
         * @return Indice de la question à ajouter
         * @see creationLevel()
         */
        clearScreen();
        String choices = "";
        int min = 0;
        println("======= Page ("+ (page + 1) + "/" + (length(questionList) / 9 + 1) + ") =======");
        if (page == 0) {min = 1;}
        else {choices = "0. Page precedente\n";}

        int limite;

        if (length(questionList) - (page * 8) > 8) {limite = 9;}
        else {limite = length(questionList) - (page * 8); }

        int indice = 1;

        while(indice < limite) {
            choices += "" + indice + ". Question " + (indice + (page * 8)) + " (" + questionList[indice + (page * 8)].question + ")\n";
            indice++;
        }

        if (limite == 9) {
            choices += "9. Page suivante\n";
        }
        choices += "==========================";
        println(choices);
        int saisie = saisieNombreEntier(min, limite, "Sélectionnez un niveau : ");
        if(saisie == 0) {
            return selecLevel(page-1);
        }
        if(saisie == 9) {
            return selecLevel(page+1);
        }
        return saisie + (page*8);
    }

    void creationLevel() {
        /**
         * Menu de création d'un niveau personnalisé
         * Cette fonction fait appel à selecLevel() 5 fois pour récupérer les 5 questions composant le niveau.
         * Le niveau est automatiquement sauvegardé dans la liste globale customLevels puis enregistrée dans le fichier customLevels.csv
         */
        int[] niveau = new int[5];
        affichageText(ANSI_BLUE + "Vous devez sélectionner 5 questions." + ANSI_RESET, 0);
        for(int indice = 0; indice < length(niveau); indice++) {
            affichageText(ANSI_GREY + "Sélectionnez la question " + (indice + 1) + ANSI_RESET);
            niveau[indice] = selecLevel(0);
        }
        addCustomLevel(niveau);
        affichageText("Niveau créé !", 5000);
        customModes();
    }

    // Initialisation du jeu ---------------------------------------------------------------------------------------

    void initQuestion() {
        /**
         * Initialisation des questions du fichier questions.csv dans les variables globales
         * 
         * Le fichier se situe dans le dossier files
         * Un log est effectué vérifiant le bon déroulé de l'itinialisation ainsi que le nombre de questions chargées.
         * En cas d'erreur, le jeu ne se lancera pas et un message d'erreur s'affichera, invitant l'utilisateur à contacter l'équipe de dev
         */
        // Log
        print("[LOAD] Questions");

        // Utilisation d'un try catch pour éviter les arrêts brusques en cas d'erreur
        try {
            CSVFile f = loadCSV(QUESTIONS_PATH); // Récupération des questions
            questionList = new Question[rowCount(f)]; // Stockage des questions pour la session actuelle
            for(int ligne = 0; ligne < length(questionList); ligne++) { // Lecture des questions
                Question q = new Question(); 
                String[] question = new String[columnCount(f)]; // Stockage temporaire des données
                for(int colonne = 0; colonne < columnCount(f); colonne++) { // Lecture des données
                    if(colonne > 0 && equals(question[0], "QCM")) { // Récupération des données supplémentaires pour les QCM (pour éviter un outOfBounds)
                        question[colonne] = getCell(f, ligne, colonne);
                    } else if(colonne <= 2) { // Récupération des données nécessaires
                        question[colonne] = getCell(f, ligne, colonne);
                    }
                }

                // Transfert et convertion des données lues en type Question
                q.type = stringToQuestionType(question[0]);
                q.question = question[1];
                if(q.type == QuestionType.QCM) {
                    q.answerQCM = stringToInt(question[2]);
                    q.choix1 = question[3];
                    q.choix2 = question[4];
                    q.choix3 = question[5];
                    q.choix4 = question[6];
                } else {
                    q.answerInput = question[2];
                }

                // Sauvegarde
                questionList[ligne] = q;
            }

            // Log
            println("\t" + ANSI_GREEN + "OK " + ANSI_RESET + length(questionList) + " questions trouvées");
        }
        catch (Exception e) {
            // Log de l'erreur
            loadedSuccessfully = false;
            println("\t" + ANSI_RED + "ERREUR " + ANSI_RESET + "Initialisation des questions interrompue : " + e);
        }
    }

    void initTrainingLevels() {
        /**
         * Initialisation des niveaux du fichier training.csv dans les variables globales correspondantes
         * 
         * Le fichier se situe dans le dossier files
         * Un log est effectué vérifiant le bon déroulé de l'itinialisation ainsi que le nombre de niveaux chargés.
         * En cas d'erreur, le jeu ne se lancera pas et un message d'erreur s'affichera, invitant l'utilisateur à contacter l'équipe de dev
         */
        print("[LOAD] Training  ");
        try {
            CSVFile f = loadCSV(TRAINING_LEVELS_PATH);
            trainingLevels = new int[rowCount(f)][columnCount(f)];
            for(int ligne = 0; ligne < length(trainingLevels, 1); ligne++) {
                for(int colonne = 0; colonne < length(trainingLevels, 2); colonne++) {
                    trainingLevels[ligne][colonne] = stringToInt(getCell(f, ligne, colonne));
                }
            }
            println("\t" + ANSI_GREEN + "OK " + ANSI_RESET + length(trainingLevels) + " niveaux trouvés");
        }
        catch (Exception e) {
            loadedSuccessfully = false;
            println("\t" + ANSI_RED + "ERREUR " + ANSI_RESET + "Initialisation des niveaux entraînement interrompue : " + e);
        }
    }

    void initCustomLevels() {
        /**
         * Initialisation des niveaux du fichier customLevels.csv dans les variables globales correspondantes
         * 
         * Le fichier se situe dans le dossier files
         * Un log est effectué vérifiant le bon déroulé de l'itinialisation ainsi que le nombre de niveaux chargés.
         * En cas d'erreur, le jeu ne se lancera pas et un message d'erreur s'affichera, invitant l'utilisateur à contacter l'équipe de dev
         */
        print("[LOAD] Custom Levels");
        try {
            CSVFile f = loadCSV(CUSTOM_LEVELS_PATH);
            if(rowCount(f) > 0) { customLevels = new int[rowCount(f)][columnCount(f)]; }
            for(int ligne = 0; ligne < length(customLevels, 1); ligne++) {
                for(int colonne = 0; colonne < length(customLevels, 2); colonne++) {
                    customLevels[ligne][colonne] = stringToInt(getCell(f, ligne, colonne));
                }
            }
            println("\t" + ANSI_GREEN + "OK " + ANSI_RESET + length(customLevels) + " niveaux trouvés");
        }
        catch (Exception e) {
            loadedSuccessfully = false;
            println("\t" + ANSI_RED + "ERREUR " + ANSI_RESET + "Initialisation des niveaux customs interrompue : " + e);
        }
    }

    void initPlayer() {
        /**
         * Initialisation des joueurs du fichier players.csv dans les variables globales correspondantes
         * 
         * Le fichier se situe dans le dossier files
         * Un log est effectué vérifiant le bon déroulé de l'itinialisation ainsi que le nombre de joueurs chargés.
         * En cas d'erreur, le jeu ne se lancera pas et un message d'erreur s'affichera, invitant l'utilisateur à contacter l'équipe de dev
         */
        print("[LOAD] Données joueurs");
        try {
            CSVFile f = loadCSV(PLAYERS_PATH);
            playerList = new Player[rowCount(f)];
            for(int ligne = 0; ligne < length(playerList); ligne++) {
                String[] player = new String[columnCount(f)];
                for(int colonne = 0; colonne < columnCount(f); colonne++) {
                    player[colonne] = getCell(f, ligne, colonne);
                }
                Player p = newPlayer(player[0], " ");
                p.trainingCompleted = stringToInt(player[1]);
                p.storyCompleted = stringToInt(player[2]);
                if (length(player) == 4) { // Evite les erreurs dues au changement de version
                    p.mdp = player[3];
                }
                playerList[ligne] = p;
            }
            println("\t" + ANSI_GREEN + "OK " + ANSI_RESET + length(playerList) + " joueurs trouvés");
        }
        catch (Exception e) {
            loadedSuccessfully = false;
            println("\t" + ANSI_RED + "ERREUR " + ANSI_RESET + "Initialisation des joueurs interrompue : " + e);
        }
    }

    void initDialogues() {
        /**
         * Initialisation des dialogues du fichier dialogues.txt dans les variables globales correspondantes
         * 
         * Le fichier se situe dans le dossier files
         * Un log est effectué vérifiant le bon déroulé de l'itinialisation ainsi que le nombre de dialogues chargés.
         * En cas d'erreur, le jeu ne se lancera pas et un message d'erreur s'affichera, invitant l'utilisateur à contacter l'équipe de dev
         */
        print("[LOAD] Dialogues   ");
        try {
            File f = newFile(DIALOGUES_PATH);
            int maxText = 0; // Recherche du plus grand nombre de texte dans un dialogue de niveau histoire
            int len = 1;
            int part = 1;
            while(ready(f)) {
                String l = readLine(f);
                if(equals(l, ". . .")) {
                    if(maxText < len) { maxText = len;}
                    len = 0;
                    part++;
                } else {
                    len++;
                }
            }
            f = newFile(DIALOGUES_PATH);
            dialogues = new String[part][maxText];
            int indicex = 0;
            int indicey = 0;
            while(ready(f)) {
                String lig = readLine(f);
                if(equals(lig, ". . .")) {
                    indicex++;
                    indicey = 0;
                } else {
                    dialogues[indicex][indicey] = lig;
                    indicey++;
                }
            }
            println("\t" + ANSI_GREEN + "OK " + ANSI_RESET + length(dialogues) + " dialogues trouvés");
        } catch (Exception e) {
            loadedSuccessfully = false;
            println("\t" + ANSI_RED + "ERREUR " + ANSI_RESET + "Initialisation des dialogues interrompue : " + e);
        }
    }

    boolean contains(String[] tab, String txt) {
        /**
         * Teste si le tableau contient le texte mit en paramètre
         * 
         * @param tab : Tableau de texte
         * @param txt : Chaîne de référence
         * @return Présence de txt dans tab
         */
        for(int indice = 0; indice < length(tab); indice++) {
            if(equals(tab[indice], txt)) {
                return true;
            }
        }
        return false;
    }

    void checkFiles() {
        /**
         * Vérifie si les fichiers du jeu sont bien créés.
         * Si ce n'est pas le cas, les créer.
         */
        String[] files = getAllFilesFromDirectory("./files");
        if(!contains(files, "players.csv")) {
            saveCSV(new String[0][0], PLAYERS_PATH);
        }
        if(!contains(files, "customLevels.csv")) {
            saveCSV(new String[0][0], CUSTOM_LEVELS_PATH);
        }
    }

    void algorithm() {
        /**
         * Lancement de toutes les initialisations
         * Si une initialisation ne se déroule pas comme prévu, un message d'erreur est affiché.
         * Sinon, le jeu se lance correctement
         */
        affichageText("Lancement du jeu en cours . . .", 2000);
        checkFiles();
        // Initialisation des fichiers CSV
        initQuestion();
        initTrainingLevels();
        initPlayer();
        initDialogues();
        initCustomLevels();

        // Vérification de la présence d'erreurs et lancement du jeu
        if (!loadedSuccessfully) {
            affichageText(ANSI_RED + "Une erreur fatale est survenue. Veuillez relancer le jeu. Si le problème persiste, réinstallez le jeu et contactez nos équipes." + ANSI_RESET);
        } else {
            clearScreen();
            connectionMenu();
        }
    }
}