import extensions.File;
import extensions.CSVFile;

class Main extends Program {
    final String QUESTIONS_PATH = "./files/questions.csv";
    final String TRAINING_LEVELS_PATH = "./files/trainingLevels.csv";
    final String STORY_LEVELS_PATH = "./files/storyLevels.csv";
    final String PLAYERS_PATH = "./files/players.csv";
    final String DRAPEAU_PATH = "./ressources/drapeau_en.txt";
    final String DIALOGUES_PATH = "./files/dialogues.txt";

    boolean loadedSuccessfully = true;

    int[][] trainingLevels = new int[0][0];
    int[][] storyLevels = new int[0][0];

    Question[] questionList = new Question[0];

    Player[] playerList = new Player[0];

    String[][] dialogues = new String[0][0];

    Player actualPlayer;

    // Fonctions de convertion de type

    QuestionType stringToQuestionType(String type) {
        if(equals(type, "QCM")) {
            return QuestionType.QCM;
        } else if (equals(type, "INPUT")) {
            return QuestionType.INPUT;
        } else {
            return null;
        }
    } // Conversion, si possible, d'un String en enu QuestionType

    String questionTypeToString(QuestionType type) {
        if(type == QuestionType.QCM) {
            return "QCM";
        } else if (type == QuestionType.INPUT) {
            return "INPUT";
        } else {
            return "";
        }
    } // Conversion d'un enum QuestionType en String
    
    // Fonctions d'affichage

    void affichageDrapeau() {
        clearScreen();
        extensions.File f = newFile(DRAPEAU_PATH);
        while(ready(f)) {
            println(readLine(f));
        }
    } // Cette fonction affichera un drapeau britannique en ASCII sauvegardé sur un fichier texte

    void affichageText(String txt, int delai) {
        for(int indice = 0; indice < length(txt); indice++) {
            print(charAt(txt, indice));
            delay(50);
        }
        delay(delai);
        println("");
    } // Affichage personnalisé du texte avec délai en paramètre

    void affichageText(String txt) {
        for(int indice = 0; indice < length(txt); indice++) {
            print(charAt(txt, indice));
            delay(50);
        }
        delay(1000);
        println("");
    } // Affichage personnalisé de texte

    void affichageDialogue(String[] tab) {
        for(int indice = 0; indice < length(tab); indice++) {
            affichageText(tab[indice]);
        }
    }

    // Initialisation des fichiers CSV

    void initQuestion() {
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
    } // Initialisation et conversion des questions

    void initTrainingLevels() {
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
    } // Cette fonction initialisera les variables trainingLevels à partir du fichier CSV correspondant

    void initStoryLevels() {
        print("[LOAD] Story    ");
        try {
            CSVFile f = loadCSV(STORY_LEVELS_PATH);
            storyLevels = new int[rowCount(f)][columnCount(f)];
            for(int ligne = 0; ligne < length(storyLevels, 1); ligne++) {
                for(int colonne = 0; colonne < length(storyLevels, 2); colonne++) {
                    storyLevels[ligne][colonne] = stringToInt(getCell(f, ligne, colonne));
                }
            }
            println("\t" + ANSI_GREEN + "OK " + ANSI_RESET + length(storyLevels) + " niveaux trouvés");
        }
        catch (Exception e) {
            loadedSuccessfully = false;
            println("\t" + ANSI_RED + "ERREUR " + ANSI_RESET + "Initialisation des niveaux histoire interrompue : " + e);
        }
    } // Cette fonction initialisera les variables trainingLevels à partir du fichier CSV correspondant

    void initPlayer() {
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
                if (length(player) == 4) {
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
    } // Cette fonction initialisera la variable playerList à partir du fichier CSV correspondant

    void initDialogues() {
        print("[LOAD] Dialogues   ");
        try {
            File f = newFile(DIALOGUES_PATH);
            int maxText = 0; // Recherche du plus grand nombre de texte dans un dialogue de niveau histoire
            int len = 0; 
            while(ready(f)) {
                String l = readLine(f);
                if(equals(l, "")) {
                    if(maxText < len) { maxText = len;}
                    len = 0;
                } else {
                    len++;
                }
            }
            f = newFile(DIALOGUES_PATH);
            dialogues = new String[length(storyLevels)][maxText];
            int indicex = 0;
            int indicey = 0;
            while(ready(f)) {
                String lig = readLine(f);
                if(equals(lig, "")) {
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
    } // Cette fonction initialise les dialogues pour le mode histoire
    
    // Sauvegarde des fichiers CSV

    void savePlayerCSV() {
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
    } // Sauvegarde dans le fichier players.csv de la liste des joueurs

    void addAndSavePlayer(Player p) {
        Player[] newList = new Player[length(playerList) + 1];
        for(int indice = 0; indice < length(playerList); indice++) {
            newList[indice] = playerList[indice];
        }
        newList[length(playerList)] = p;
        playerList = newList;
        savePlayerCSV();
    } // Ajoute un joueur dans la liste de player

    void saveActualPlayer() {
        println("Sauvegarde . . .");
        for(int indice = 0; indice < length(playerList); indice++) {
            if(equals(actualPlayer.username, playerList[indice].username)) {
                playerList[indice] = actualPlayer;
            }
        }
        println("Sauvegardé");
    } // Fonction modifiant le player de la playerList par le playerActuel avec la progression modifiée

    // Fonctions diverses

    boolean isYes(String txt) { return equals(txt, "o"); } // Fonction renvoyant la validation d'une question

    Player newPlayer(String nom, String mdp) {
        Player p = new Player();
        p.username = nom;
        p.trainingCompleted = 0;
        p.storyCompleted = 0;
        p.mdp = mdp;
        return p;
    } // Fonction renvoyant un nouveau joueur initialisé

    boolean contains(String txt, char carac) {
        boolean found = false;
        int indice = 0;
        while(indice < length(txt) && !found) {
            found = charAt(txt, indice) == carac;
            indice++;
        }
        return found;
    } // Fonction renvoyant la présence d'un caractère dans une chaîne

    boolean playerExists(String name) {
        for(int indice = 0; indice < length(playerList); indice++) {
            if(equals(playerList[indice].username, name)) {
                return true;
            }
        }
        return false;
    } // Cette fonction renvoie true si le joueur de nom existe, false sinon

    Player getPlayerByName(String name) {
        for(int indice = 0; indice < length(playerList); indice++) {
            if(equals(playerList[indice].username, name)) {
                return playerList[indice];
            }
        }
        return null;
    } // Cette fonction renvoie le player correspondant au nom, null si aucun player ne correspond

    // Menus et fonctions principales

    String cryptage(String txt, char key) {
        String mdp = "";
        for(int indice = 0; indice < length(txt); indice++) {
            mdp = mdp + (char)(charAt(txt, indice) + key);
        }
        return mdp;
    }

    String newMDP() {
        print("Entrez un nouveau mot de passe : ");
        String saisie = saisieReponse();
        print("Confirmez votre mot de passe : ");
        String saisie2 = saisieReponse();
        if (!equals(saisie, saisie2)) {
            println("Les deux mots de passe ne correspondent pas.");
            return newMDP();
        } else {
            return cryptage(saisie, '¤');
        }
    }

    void connectionMenu() {
        affichageDrapeau();
        affichageText(ANSI_RED + "Bienvenue sur DoubleLangue !" + ANSI_RESET + "\nPour commencer, merci d'entrer votre nom d'utilisateur.\n" + ANSI_BLUE + "Votre progression sera sauvegardée par le biais de ce nom." + ANSI_RESET);
        print("Nom d'utilisateur (exit pour quitter) : ");
        String nom = toUpperCase(readString());
        if (playerExists(nom)) {
            actualPlayer = getPlayerByName(nom);
            affichageText("Utilisateur " + actualPlayer.username + " trouvé.");
            
            // Vérification que les niveaux complétés ne dépassent pas le nombre de niveaux existants 
            if (actualPlayer.trainingCompleted > length(trainingLevels)) {
                actualPlayer.trainingCompleted = 0;
                affichageText("Une incohérence a été remarquée dans les niveaux d'entraînements complétés. Nous avons remis tout ça en ordre ne vous en faites pas ;)");
            }
            if (actualPlayer.storyCompleted > length(storyLevels)) {
                actualPlayer.storyCompleted = 0;
                affichageText("Une incohérence a été remarquée dans les niveaux histoire complétés. Nous avons remis tout ça en ordre ne vous en faites pas ;)");
            }
            if (equals(actualPlayer.mdp, " ")) {
                affichageText("Aucun mot de passe n'a été définit.");
                actualPlayer.mdp = newMDP();
            } else {
                affichageText("Entrez votre mot de passe : ");
                String saisie = saisieReponse();
                if (!equals(cryptage(saisie, '¤'), actualPlayer.mdp)) {
                    affichageText("Mot de passe incorrect. Si vous avez oublié votre mot de passe, contactez nos équipes.");
                    connectionMenu();
                    return;
                }
            }
            mainMenu();
        } else {
            if (equals(nom, "EXIT")) {
                affichageText("Fermeture du jeu . . .");
                savePlayerCSV();
                println("[SAVE] Joueurs sauvegardés");
                System.exit(0);
            } else {
                affichageText("Aucun utilisateur trouvé.");
                print("Souhaitez-vous créer un nouvel utilisateur du nom de " + nom + " ? (o/n) ");
                String validation = saisieReponse();
                if (isYes(validation)) {
                    String mdp = newMDP();
                    affichageText("Création du compte en cours . . .");
                    Player p = newPlayer(nom, mdp);
                    addAndSavePlayer(p);
                    actualPlayer = p;
                    affichageText("Compte créé ! Votre progression sera sauvegardée !");
                    affichageText("Bienvenue sur DoubleLangue !");
                    mainMenu();
                } else {
                    connectionMenu();
                }
            }
        }
        
    } // Cette fonction affichera le menu de connexion. Elle fera appel à la fonction saisieConnection, playerExists et getPlayerByName. Elle initialisera également la valeur actualPlayer 
    
    void mainMenu() {
        affichageDrapeau();
        affichageText("Bienvenue " + ANSI_GREEN + actualPlayer.username + ANSI_RESET + "!");
        affichageText("Choisissez votre mode de jeu :\n0. Déconnexion\n1. Mode histoire (Progression : " + (actualPlayer.storyCompleted * 100 / length(storyLevels)) + " %)\n2. Mode entraînement (Progression : " + (actualPlayer.trainingCompleted * 100 / length(trainingLevels)) + " %)\n3. Paramètres");
        int option = saisieNombreEntier(3);
        switch (option) {
            case 0:
                affichageText("Votre progression va être sauvegardée. Veuillez patienter . . .");
                saveActualPlayer();
                affichageText("Progression sauvegardée ! A très vite !");
                connectionMenu();
            case 1:
                clearScreen();
                playStory(actualPlayer.storyCompleted);
            case 2:
                trainingModeSelection();
            case 3:
                settings();
        }
    } // Menu de sélection du mode de jeu

    int saisieNombreEntier(int maxOption) {
        boolean valide = false;
        String saisie = "";
        while(!valide) {
            saisie = readString();
            if(length(saisie) == 1 && contains("0123456789", charAt(saisie, 0)) && stringToInt(saisie) <= maxOption) {
                valide = true;
            } else {
                println("Saisie incorrecte.");
            }
        }
        return stringToInt(saisie);
    } // Contrôle la saisie d'un nombre entier

    boolean isGoodInputAnswer(Question q, String a) {
        a = toUpperCase(a); // Transformation de la réponse en une réponse évitant les problèmes de casse et de ponctuation
        q.answerInput = toUpperCase(q.answerInput); // Même opération pour les questions stockées dans l'optique d'une nouvelle fonctionnalité de questions personnalisées (et facilitant la lecture en bdd)
        String res = "";
        for(int indice = 0; indice < length(a); indice++) {
            char carac = charAt(a, indice);
            if (carac >= 'A' && carac <= 'Z') {
                res += carac;
            }
        }
        String ans = "";
        for(int indice = 0; indice < length(q.answerInput); indice++) {
            char carac = charAt(q.answerInput, indice);
            if (carac >= 'A' && carac <= 'Z') {
                ans += carac;
            }
        }
        if (equals(ans, res)) {
            return true;
        }
        return false;
    } // Vérification de la validité ou non d'une réponse au INPUT

    boolean isGoodQCMAnswer(Question q, int a) {
        return q.answerQCM + 1 == a;
    } // Vérification de la validité ou non d'une réponse au QCM


    void trainingModeSelection() {
        if(actualPlayer.trainingCompleted == 0) {
            affichageText("Bienvenue dans le module d'entraînement.");
            affichageText("Ici, vous trouverez des niveaux pour vous entraîner au mode histoire.");
            affichageText("Commençons par le premier niveau !");
            playTraining(0);
        } else {
            affichageText("Lors de votre dernière session, vous êtes arrivés au niveau " + actualPlayer.trainingCompleted + " sur les " + length(trainingLevels) + " disponibles.");
            int max;
            if(actualPlayer.trainingCompleted >= length(trainingLevels)) {
                affichageText("0. Retour\n1. Rejouer un niveau");
                max = 1;
            } else {
                affichageText("0. Retour\n1. Rejouer un niveau\n2. Continuer vers le niveau " + (actualPlayer.trainingCompleted + 1));
                max = 2;
            }
            int saisie = saisieNombreEntier(2);
            switch(saisie) {
                case 0:
                    saveActualPlayer();
                    savePlayerCSV();
                    mainMenu();
                case 1:
                    affichageText("Vous pouvez rejouer un niveau que vous avez déjà terminé. Choisissez un niveau : ", 0);
                    trainingSelection(0);
                case 2:
                    if(actualPlayer.trainingCompleted < length(trainingLevels)) {playTraining(actualPlayer.trainingCompleted);}
                    else {
                        affichageText("Vous avez terminé tous les niveaux. Vous pouvez les rejouer ou attendre une prochaine mise à jour.");
                        trainingModeSelection();
                    }
                default:
                    affichageText("Veuillez réessayer");
                    trainingModeSelection();
            }
        }
    } // Fonction affichant le choix du niveau à jouer

    void trainingSelection(int page) { 
        String choices;
        affichageText("======= Page ("+ (page + 1) + "/" + (length(trainingLevels) / 8 + 1) + ") =======", 0);
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
            choices += "9. Page suivante";
            limite++;
        }
        choices += "==========================";
        affichageText(choices);

        int saisie = saisieNombreEntier(limite);
        switch(saisie) {
            case 0:
                if(page == 0) {trainingModeSelection();}
                else {trainingSelection(page-1); }
            case 9:
                trainingSelection(page+1);
            default:
                playTraining(saisie + 8 * page);
        }
    } // Fonction affichant la sélection du niveau d'entraînement déjà terminé

    int play(Question[] niveau, int tentatives) {

        int question = 0;

        while (question < length(niveau) && tentatives > 0) {
            if(niveau[question].type == QuestionType.QCM) {
                affichageQCM(niveau[question], question + 1);
                while(tentatives > 0 && !isGoodQCMAnswer(niveau[question], saisieNombreEntier(4))) {
                    tentatives -= 1;
                    affichageText("Mauvaise réponse. Il vous reste " + tentatives + " tentative(s)");
                }
            } else {
                affichageInput(niveau[question], question + 1);
                while(tentatives > 0 && !isGoodInputAnswer(niveau[question], saisieReponse())) {
                    tentatives -= 1;
                    affichageText("Mauvaise réponse. Il vous reste " + tentatives + " tentative(s)");
                }
            }
            if (tentatives > 0) {
                affichageText("Bonne réponse !");
                question++;
            }
        }

        return tentatives;
    }

    void playTraining(int level) { 
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
            if (actualPlayer.trainingCompleted == level) { // Affichage si c'est la première fois que le niveau est gégné
                actualPlayer.trainingCompleted++;
                affichageText("Congrats ! Vous avez complètement terminé ce niveau ! Vous pouvez désormais jouer au niveau " + (actualPlayer.trainingCompleted + 1));
            } else { // Affichage si la personne avait déjà gagné le niveau précédemment
                affichageText("Congrats ! Vous avez complètement terminé ce niveau ! Vous n'avez pas perdu la main ;)");
            }
            // Sauvegarde du joueur
            saveActualPlayer();
            savePlayerCSV();
            trainingModeSelection();
        } else {
            affichageText("Dommage... Vous n'avez pas terminé ce niveau. Vous pouvez toujours réessayer");
            mainMenu();
        }
    } // Fonction jouant les questions d'un niveau

    void playStory(int level) {
        if (length(storyLevels) == actualPlayer.storyCompleted) {
            affichageText("Vous avez terminé le mode histoire ! Félicitations ! Souhaitez-vous recommencer ? Attention ! Votre progression sera écrasée ! (o/n)");
            if(isYes(saisieReponse())) {
                actualPlayer.storyCompleted = 0;
                affichageText("Progression écrasée. See you soon et bonne chance dans votre quête !");
            } else {
                affichageText("Vous pourrez recommencer l'histoire quand vous voulez en revenant ici ! See you soon !");
            }
            saveActualPlayer();
            savePlayerCSV();
            mainMenu();
            return;
        }
        affichageDialogue(dialogues[level]);
        Question[] niveau = new Question[length(storyLevels, 2)];
        int tentatives = 5;

        // Récupération des questions
        for(int indice = 0; indice < length(niveau); indice++) {
            niveau[indice] = questionList[storyLevels[level][indice]];
        }

        // Lancement du jeu
        tentatives = play(niveau, tentatives);

        // Vérification victoire ou défaite
        if(tentatives > 0) {
            actualPlayer.storyCompleted++;
            if (actualPlayer.storyCompleted == length(storyLevels)) {
                affichageText("Vous avez terminé le mode histoire ! Félicitations ! Souhaitez-vous recommencer ? Attention ! Votre progression sera écrasée ! (o/n)");
                if(isYes(saisieReponse())) {
                    actualPlayer.storyCompleted = 0;
                    affichageText("Progression écrasée. See you soon et bonne chance dans votre quête !");
                } else {
                    affichageText("Vous pourrez recommencer l'histoire quand vous voulez en revenant ici ! See you soon !");
                }
                saveActualPlayer();
                savePlayerCSV();
                mainMenu();
                return;
            }
            saveActualPlayer();
            affichageText("Chapitre " + actualPlayer.storyCompleted + " terminé. Souhaitez-vous continuer ? (o/n)");
            if (isYes(saisieReponse())) {
                playStory(actualPlayer.storyCompleted);
            } else {
                affichageText("See you soon !");
                savePlayerCSV();
                mainMenu();
            }
        } else {
            affichageText("You failed. Vous n'avez pas réussi à terminer votre quête. Retentez votre chance après vous être entraîné !", 3000);
            mainMenu();
        }
    }

    void settings() {
        affichageText("Choisissez un paramètre :\n0. Retour\n1. Modifier votre pseudo\n2. Réinitialiser le profil\n3. Définir un nouveau mot de passe.");
        int entree = saisieNombreEntier(3);
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
                actualPlayer.mdp = newMDP();
                affichageText("Mot de passe modifié.");
                settings();
                return;
        }
    }

    void editUsername() {
        affichageText("Entrez votre nouveau nom d'utilisateur (entrez annuler pour revenir en arrière) : ");
        String newUsername = toUpperCase(saisieReponse());
        if (equals(newUsername, "annuler")) {
            affichageText("Annulation . . .");
            settings();
        } else if(playerExists(newUsername)) {
            affichageText("Nom d'utilisateur déjà utilisé.");
            editUsername();
        } else {
            affichageText("Êtes vous sur de vouloir modifier votre nom d'utilisateur (Ancien : " + actualPlayer.username + " / Nouveau : " + newUsername + ") ? (o/n)");
            if(isYes(saisieReponse())) {
                actualPlayer.username = newUsername;
                saveActualPlayer();
                affichageText("Votre nom d'utilisateur a été mis à jour !");
            } else {
                affichageText("Annulation . . .");
            }
            settings();
        }
    }

    void doubleCheckReset() {
        affichageText("Êtes-vous sur de vouloir réinitialiser votre profil ? Cette action est irréversible. (o/n)");
        if(isYes(saisieReponse())) {
            affichageText("Dernière chance d'annuler la réinitialisation. Souhaitez-vous vraiment tout supprimer ? (o/n)");
            if(isYes(saisieReponse())) {
                actualPlayer = newPlayer(actualPlayer.username, actualPlayer.mdp);
                saveActualPlayer();
                savePlayerCSV();
                affichageText("Progression réinitialisée.", 2000);
            }
        }
        settings();
    }

    void affichageInput(Question q, int nbQuestion) {
        affichageText("Question " + nbQuestion + " : " + q.question);
        print("Votre réponse : ");
    } // Méthode affichant les questions de type INPUT

    void affichageQCM(Question q, int nbQuestion) {
        affichageText("Question " + nbQuestion + " : " + q.question + "\n1. " + q.choix1 + "\n2. " + q.choix2 + "\n3. " + q.choix3 + "\n4. " + q.choix4);
        print("Votre réponse : ");
    } // méthode affichant les questions de type QCM (avec affichage des possibilités)

    String saisieReponse() {
        return readString();
    } // Fonction renvoyant l'entrée utilisateur

    void algorithm() {
        affichageText("Lancement du jeu en cours . . .", 2000);
        // Initialisation des fichiers CSV
        initQuestion();
        initTrainingLevels();
        initStoryLevels();
        initPlayer();
        initDialogues();

        // Vérification de la présence d'erreurs et lancement du jeu
        if (!loadedSuccessfully) {
            affichageText(ANSI_RED + "Une erreur fatale est survenue. Veuillez relancer le jeu. Si le problème persiste, réinstallez le jeu et contactez nos équipes." + ANSI_RESET);
        } else {
            clearScreen();
            connectionMenu();
        }
    }
}