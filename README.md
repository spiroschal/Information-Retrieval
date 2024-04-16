# Information-Retrieval
##Εισαγωγή
Σε αυτό το project θα υλοποιήσουμε ένα σύστημα αναζήτησης πληροφορίας από επιστημονικά άρθρα. Τα εργαλεία που θα χρησιμοποιήσουμε είναι η βιβλιοθήκη Lucene που θα μας βοηθήσει στην ανάκτηση πληροφορίας μέσω μηχανών αναζήτησης κειμένου και όλο αυτό θα υλοποιηθεί πάνω σε γλώσσα JAVA.

Συλλογή Εγγράφων (corpus)
Ένα από τα πρώτα και σημαντικά βήματα είναι η συλλογή των εγγράφων μας, όπου στην συγκεκριμένη εργασία τα έγγραφα που θέλουμε είναι επιστημονικά άρθρα. Μια συλλογή τέτοιων άρθρων θα γίνει από το διαδίκτυο και πιο συγκεκριμένα από το site της σελίδας Kaggle, με link https://www.kaggle.com/datasets/rowhitswami/nips-papers-1987-2019-updated/data?select=papers.csv. Τα 2 αρχεία που συναντάμε, είναι τύπου ‘.csv’, και το ένα αφορά τα άρθρα έχοντας 5 στήλες(source_id, year, title, abstract, full_text) και το άλλο αφορά τους συγγραφής έχοντας 4 στήλες(source_id, first_name, last_name, institution), και συνδέονται μεταξύ τους αυτά τα 2 αρχεία με ένα κοινό κλειδί βάση των source_ids. Κυρίως θα ασχοληθούμε με το αρχείο που αφορά τα άρθρα και σε 2ο χρόνο (στο προαιρετικό ερώτημα) θα χρειαστεί να πάρουμε και τα δεδομένα από το άλλο αρχείο με τους συγγραφής.
Μια σημαντική προεργασία που πρέπει να γίνει προτού περάσουμε στα επόμενα βήματα, είναι να φιλτράρουμε/καθαρίσουμε κάποιες πλειάδες από τους πίνακές μας που κάποιες στήλες τους μπορεί να έχουν missing values γιατί θα μας χαλάει το αποτελέσματά μας.

Ανάλυση κειμένου και Κατασκευή ευρετηρίου
Αρχικά, καλό είναι να παραθέσουμε τα πεδία που θα χρειαστούμε και για την κατασκευή των ευρετηρίων. Τα πεδία είναι σίγουρα τα: title, abstract, full_text και ίσως αν θέλουμε να κάνουμε κάποια ερώτηση με την χρονολογία θα χρειαστούμε και το year (αυτά είναι μόνο από τα άρθρα που αρχικά αυτά θέλουμε).
Για την ανάλυση κειμένου θα χρησιμοποιήσουμε από την Lucene το Standard Analyzer, ένα είδος που ικανοποιεί σε μεγάλο βαθμό τις ανάγκες μας για τα συγκεκριμένα άρθρα που έχουμε πάρει από το corpus, όπου με αυτό έχουμε την δυνατότητα να διαχωρίσουμε την κάθε λέξη, να την επεξεργαστούμε κάνοντας lowercasing όλα τα γράμματά της και να αφαιρέσουμε από το κείμενο τα σημεία στίξης τα οποία δεν είναι και πολύ σημαντικά στην κατασκευή του ευρετηρίου μας.
Για να βάλουμε στο ευρετήριο μας τις εγγραφές θα χρησιμοποιήσουμε την IndexWriter και αν θέλουμε να τα αποθηκεύσουμε και στον δίσκο θα χρησιμοποιήσουμε to FSDirectory.

Αναζήτηση
Το πρόγραμμά μας πρέπει να υποστηρίζει 3 διαφορετικά είδη αναζήτησεις: (α) (β) (γ)
Επίσης θα έχει την ικανότητα να κρατάει ένα ιστορικό αναζητήσεων για να μπορεί να προσωποποιεί την αναζήτηση για τον κάθε χρήστη. Με αυτόν τον τρόπο κερδίζουμε είτε το ότι μπορεί ο χρήστης να βλέπει παλιές του αναζητήσεις είτε με το να γίνεται αυτόματη συμπλήρωση μιας λέξη που πιθανόν να θέλει καθώς πληκτρολογεί.

Παρουσίαση Αποτελεσμάτων
Το τελευταίο βήμα είναι η οπτικοποιημένη απόδοση του project μας, με σκοπό να παίρνει κάποιες εισόδου από τον χρήστη και με βάση αυτά να εμφανίζει τα επιθυμητά αποτελέσματα που του ζητάει ο χρήστης.
Αρχικά ο χρήστης θα βλέπει ένα text placeholder όπου θα μπορεί, με μορφή κειμένου, να περάσει όποια παράμετρο/λέξη θέλει. Από έναν drop-down menu, ο χρήστης θα έχει την δυνατότητα να επιλέξει έναν από τους τρεις (ή 4 αν γίνει και το προαιρετικό κομμάτι) τρόπους αναζήτησης που θα έχουν υλοποιηθεί, όπως αναφέραμε παραπάνω. Επίσης θα έχει ένα button όπου όταν ο χρήστης έχει γράψει αυτό  που θέλει, πατώντας το, θα του παράγει τα αποτελέσματα που επιθυμεί, δηλαδή το ή τα επιστημονικά άρθρα που ψάχνει.
