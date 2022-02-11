package fr.miage.fsgbd;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * @author Galli Gregory, Mopolo Moke Gabriel
 * @param <Type>
 */
public class BTreePlus<Type> implements java.io.Serializable {
    private Noeud<Type> racine;

    public BTreePlus(int u, Executable e) {
        racine = new Noeud<Type>(u, e, null);
    }

    public void afficheArbre() {
        racine.afficheNoeud(true, 0);
    }

    /**
     * Méthode récursive permettant de récupérer tous les noeuds
     *
     * @return DefaultMutableTreeNode
     */
    public DefaultMutableTreeNode bArbreToJTree()
    {
        return bArbreToJTree(racine);
    }

    private DefaultMutableTreeNode bArbreToJTree(Noeud<Type> root) {
        StringBuilder txt = new StringBuilder();
        for (Type key : root.keys) txt.append(key.toString()).append(" | ");
        DefaultMutableTreeNode racine2 = new DefaultMutableTreeNode(txt.toString(), true);
        for (Noeud<Type> fil : root.fils) racine2.add(bArbreToJTree(fil));

        return racine2;
    }

    public void construct()
    {
        construct(this.racine); // construit les liens entre feuilles
        System.out.println("");
        System.out.println("Construction Test : ");
        test(this.racine); // test des liens et des pointeurs
        System.out.println("---------------");
        System.out.println("");
    }

    private void construct(Noeud<Type> n) //traverse toute l'arbre. l'idéal serait de faire ça pendant le génération de l'arbre
    {
        if (n.fils.size() == 0) n.isLeafNode(); // si le noeud est une feuille

        // si le noeud n'est pas une feuille il faudrait enlever les pointeurs
        // car dans un b+ arbres les pointeurs sont stockés seulement dans les feuilles
        else n.isNotLeafNode();

        for (int i = 0; i < n.fils.size(); i++) construct(n.fils.get(i));
    }

    private void test(Noeud<Type> n)
    {
        System.out.println("---------------");
        System.out.println(n.keys + " ptrs = " + n.ptrs);
        if (n.next != null)
        {
            System.out.println(n.keys + " is a leafNode. Next leafNodes keys : " + n.next.keys);
        }
        for (int i = 0; i < n.fils.size(); i++)
        {
            test(n.fils.get(i));
        }
    }

    public boolean search(Type id)
    {
        return search(this.racine, id);
    }

    private boolean search(Noeud<Type> ln, Type id)
    {
        if (ln.fils.size() == 0) // verifie si on est dans une feuille
        {
            // on est dans la feuille contenant la clé qu'on cherche
            int found = ln.binarySearch((int)(id));// recherche dichotomique pour trouver la clé dans le noeud
            if (found == -1) System.out.println("bug = " + ln.keys);
            //System.out.println("found it. " + ln.keys.get(found) + "'s ptr is = " + ln.ptrs.get(found));
            return true;
        }
        for (int idx = 0; idx < ln.keys.size(); idx++) // itére sur les clés du noeud
        {
            if ((int)(ln.keys.get(idx)) > (int)(id)) // si clé cherchée est inferieur au clé courante
            {
                return search(ln.fils.get(idx), id); // fils gauche
            }
            else if ((int)(ln.keys.get(idx)) < (int)(id)) // si clé cherchée est superieur au clé courante
            {
                if (ln.keys.size() > idx + 1){ // verifie qu'il y a une clé apres la clé courante
                    if ((int)(ln.keys.get(idx + 1)) > (int)(id)) // si clé cherché est inferieur à la prochaine clé du noeud
                    {
                        return search(ln.fils.get(idx + 1), id); // fils droit
                    }
                }
                else // si dernier clé du noeud
                {
                    return search(ln.fils.get(idx + 1), id); // fils droit
                }
            }
            else if ((int)(ln.keys.get(idx)) == (int)(id)) // si clé courante est la clé qu'on cherche mais on est pas dans une feuille
            {
                return search(ln.fils.get(idx + 1), id); // fils droit
            }
        }
        return false;
    }


    public boolean addValeur(Type valeur) {
        if (racine.contient(valeur) == null) {
            Noeud<Type> newRacine = racine.addValeur(valeur);
            if (racine != newRacine)
                racine = newRacine;
            return true;
        }
        return false;
    }

    public boolean addValeur(Type valeur, int pointeur) {
        if (racine.contient(valeur) == null) {
            Noeud<Type> newRacine = racine.addValeur(valeur, pointeur);
            if (racine != newRacine)
                racine = newRacine;
            return true;
        }
        return false;
    }


    public void removeValeur(Type valeur) {
        System.out.println("Retrait de la valeur : " + valeur.toString());
        if (racine.contient(valeur) != null) {
            Noeud<Type> newRacine = racine.removeValeur(valeur, false);
            if (racine != newRacine)
                racine = newRacine;
        }
    }
}
