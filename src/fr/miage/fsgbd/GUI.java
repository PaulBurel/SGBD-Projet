package fr.miage.fsgbd;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Galli Gregory, Mopolo Moke Gabriel
 */
public class GUI extends JFrame implements ActionListener {
    private static final String COMMA_DELIMITER = ",";
    TestInteger testInt = new TestInteger();
    //TODO doit dependre du type de colonne à indexer
    BTreePlus<Integer> bInt;
    private JButton buttonClean, buttonRemove, buttonLoad, buttonSave, buttonAddMany, buttonAddItem, buttonRefresh, buttonaddFile, buttonSearch;
    private JTextField txtNbreItem, txtNbreSpecificItem, txtU, txtFile, removeSpecific, txtColNb, txtSearchLine;
    private final JTree tree = new JTree();
    ArrayList<ArrayList<String>> records = new ArrayList<>();

    public GUI() {
        super();
        build();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonLoad || e.getSource() == buttonClean || e.getSource() == buttonSave || e.getSource() == buttonRefresh) {
            if (e.getSource() == buttonLoad) {
                BDeserializer<Integer> load = new BDeserializer<Integer>();
                bInt = load.getArbre(txtFile.getText());
                if (bInt == null)
                    System.out.println("Echec du chargement.");

            } else if (e.getSource() == buttonClean) {
                if (Integer.parseInt(txtU.getText()) < 2)
                    System.out.println("Impossible de cr?er un arbre dont le nombre de cl?s est inf?rieur ? 2.");
                else
                    bInt = new BTreePlus<Integer>(Integer.parseInt(txtU.getText()), testInt);
            } else if (e.getSource() == buttonSave) {
                BSerializer<Integer> save = new BSerializer<Integer>(bInt, txtFile.getText());
            }else if (e.getSource() == buttonRefresh) {
                tree.updateUI();
            }
        } else {
            if (bInt == null)
                bInt = new BTreePlus<Integer>(Integer.parseInt(txtU.getText()), testInt);

            if (e.getSource() == buttonaddFile) {
                this.records = new ArrayList<>();
                JFileChooser fc = new JFileChooser();
                File fileToBeSent = null;
                int x = fc.showOpenDialog(null);
                if (x == JFileChooser.APPROVE_OPTION) { // si fichier valide
                    fileToBeSent = fc.getSelectedFile();
                    buttonaddFile.setEnabled(true);
                    try (Scanner scanner = new Scanner(fileToBeSent);) {
                        while (scanner.hasNextLine()) {
                            this.records.add(getRecordFromLine(scanner.nextLine()));
                        }
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }
                } else {
                    fileToBeSent = null;
                    buttonaddFile.setEnabled(false);
                }
                System.out.println("cols in csv : " + this.records.get(0).size() + " colnames = " + this.records.get(0));
                System.out.println("rows in csv = " + (this.records.size() - 1));

                // TODO verifier si le col de colnb est bien int
                fileLoaded(Integer.parseInt(txtColNb.getText()));

            }

            if (e.getSource() == buttonSearch)
            {
                if (records.size() > 0)
                {
                    int nbCol = Integer.parseInt(txtColNb.getText());
                    Random rand = new Random();
                    int idx = rand.nextInt(records.size());
                    int idToSearch = Integer.parseInt(records.get(idx).get(nbCol));
                    long startTime = System.nanoTime();
                    linearSearch(idToSearch);
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime);
                    System.out.println("linear search duration = " + duration + " nanoseconds");
                    startTime = System.nanoTime();
                    indexSearch(idToSearch);
                    endTime = System.nanoTime();
                    duration = (endTime - startTime);
                    System.out.println("index search duration = " + duration + " nanoseconds");
                }
                else
                {
                    System.out.println("Chargez d'abord un fichier");
                }
            }

            if (e.getSource() == buttonAddMany) {
                addMany(Integer.parseInt(txtNbreItem.getText()));

            } else if (e.getSource() == buttonAddItem) {
                if (!bInt.addValeur(Integer.parseInt(txtNbreSpecificItem.getText())))
                    System.out.println("Tentative d'ajout d'une valeur existante : " + txtNbreSpecificItem.getText());
                txtNbreSpecificItem.setText(
                        String.valueOf(
                                Integer.parseInt(txtNbreSpecificItem.getText()) + 2
                        )
                );

            } else if (e.getSource() == buttonRemove) {
                bInt.removeValeur(Integer.parseInt(removeSpecific.getText()));
            }
        }

        tree.setModel(new DefaultTreeModel(bInt.bArbreToJTree()));
        for (int i = 0; i < tree.getRowCount(); i++)
            tree.expandRow(i);

        tree.updateUI();
    }

    private ArrayList<String> getRecordFromLine(String line) {
        ArrayList<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    private void fileLoaded(int colnb) {
        ArrayList<Integer> val = new ArrayList<>();
        ArrayList<Integer> pointeurs = new ArrayList<>();
        for (int i = 1; i < this.records.size(); i++){
            val.add(Integer.parseInt(records.get(i).get(colnb)));
            pointeurs.add(i); //on garde la "ligne" dans le csv
        }
        addMany(val, pointeurs);
    }

    private void addMany(int nb) {
        for (int i = 0; i < nb; i++) {
            int valeur = (int) (Math.random() * 10 * nb);
            boolean done = bInt.addValeur(valeur);

					/*
					  On pourrait forcer l'ajout mais on risque alors de tomber dans une boucle infinie sans "r?gle" faisant sens pour en sortir

					while (!done)
					{
						valeur =(int) (Math.random() * 10 * Integer.parseInt(txtNbreItem.getText()));
						done = bInt.addValeur(valeur);
					}
					 */
        }
    }

    private void addMany(ArrayList<Integer> valeurs, ArrayList<Integer> pointeurs) {
        for (int i = 0; i < valeurs.size(); i++) {
            boolean done = bInt.addValeur(valeurs.get(i), pointeurs.get(i));

					/*
					  On pourrait forcer l'ajout mais on risque alors de tomber dans une boucle infinie sans "r?gle" faisant sens pour en sortir

					while (!done)
					{
						valeur =(int) (Math.random() * 10 * Integer.parseInt(txtNbreItem.getText()));
						done = bInt.addValeur(valeur);
					}
					 */
        }
    }

    private int linearSearch(int id)
    {
        int nbCol = Integer.parseInt(txtColNb.getText());
        for (int i = 1; i < records.size(); i++)
        {
            if (id == Integer.parseInt(records.get(i).get(nbCol)))
            {
                System.out.println("found it. " + id + "'s ptr is = " + i);
                return i;
            }
        }
        return -1;
    }

    private boolean indexSearch(int id)
    {
        return bInt.search(id);
    }


    private void build() {
        setTitle("Indexation - B Arbre");
        setSize(760, 760);
        setLocationRelativeTo(this);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(buildContentPane());
    }

    private JPanel buildContentPane() {
        GridBagLayout gLayGlob = new GridBagLayout();

        JPanel pane1 = new JPanel();
        pane1.setLayout(gLayGlob);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 2, 0);

        JLabel labelU = new JLabel("Nombre max de clés par noeud (2m): ");
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        pane1.add(labelU, c);

        txtU = new JTextField("4", 7);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 2;
        pane1.add(txtU, c);

        JLabel labelBetween = new JLabel("Nombre de clefs à ajouter:");
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        pane1.add(labelBetween, c);

        txtNbreItem = new JTextField("10000", 7);
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        pane1.add(txtNbreItem, c);


        buttonAddMany = new JButton("Ajouter n éléments aléatoires à l'arbre");
        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonAddMany, c);

        JLabel labelSpecific = new JLabel("Ajouter une valeur spécifique:");
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(labelSpecific, c);

        txtNbreSpecificItem = new JTextField("50", 7);
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(txtNbreSpecificItem, c);

        buttonAddItem = new JButton("Ajouter l'élément");
        c.gridx = 2;
        c.gridy = 3;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonAddItem, c);

        JLabel labelRemoveSpecific = new JLabel("Retirer une valeur spécifique:");
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(labelRemoveSpecific, c);

        removeSpecific = new JTextField("54", 7);
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(removeSpecific, c);

        buttonRemove = new JButton("Supprimer l'élément n de l'arbre");
        c.gridx = 2;
        c.gridy = 4;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonRemove, c);

        JLabel labelFilename = new JLabel("Nom de fichier : ");
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(labelFilename, c);

        txtFile = new JTextField("arbre.abr", 7);
        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(txtFile, c);

        buttonSave = new JButton("Sauver l'arbre");
        c.gridx = 2;
        c.gridy = 5;
        c.weightx = 0.5;
        c.gridwidth = 1;
        pane1.add(buttonSave, c);

        buttonLoad = new JButton("Charger l'arbre");
        c.gridx = 3;
        c.gridy = 5;
        c.weightx = 0.5;
        c.gridwidth = 1;
        pane1.add(buttonLoad, c);

        buttonClean = new JButton("Reset");
        c.gridx = 2;
        c.gridy = 8;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonClean, c);

        buttonRefresh = new JButton("Refresh");
        c.gridx = 2;
        c.gridy = 9;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonRefresh, c);

        JLabel labelAddFile = new JLabel("Numero de la colonne à indexer : ");
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(labelAddFile, c);

        txtColNb = new JTextField("0", 7);
        c.gridx = 1;
        c.gridy = 6;
        c.weightx = 1;
        pane1.add(txtColNb, c);

        buttonaddFile = new JButton("Load File");
        c.gridx = 2;
        c.gridy = 6;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonaddFile, c);

        buttonSearch = new JButton("Statistiques de recherche");
        c.gridx = 2;
        c.gridy = 7;
        c.weightx = 0.5;
        c.gridwidth = 2;
        pane1.add(buttonSearch, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 400;       //reset to default
        c.weighty = 1.0;   //request any extra vertical space
        c.gridwidth = 4;   //2 columns wide
        c.gridx = 0;
        c.gridy = 10;

        JScrollPane scrollPane = new JScrollPane(tree);
        pane1.add(scrollPane, c);

        tree.setModel(new DefaultTreeModel(null));
        tree.updateUI();

        txtNbreItem.addActionListener(this);
        buttonAddItem.addActionListener(this);
        buttonAddMany.addActionListener(this);
        buttonLoad.addActionListener(this);
        buttonSave.addActionListener(this);
        buttonRemove.addActionListener(this);
        buttonClean.addActionListener(this);
        buttonRefresh.addActionListener(this);
        buttonaddFile.addActionListener(this);
        buttonSearch.addActionListener(this);

        return pane1;
    }
}

