package fr.miage.fsgbd;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Galli Gregory, Mopolo Moke Gabriel
 * @param <Type>
 */
public class BTreePlus<Type> implements java.io.Serializable {
    private Noeud<Type> racine;
    private Noeud<Type> previous;
    private Map<Type, Integer> ptrs = new HashMap<>();
    private boolean reload;

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
        if (previous != null)
        {
            reload = true;
        }
        return bArbreToJTree(racine);
    }

    private DefaultMutableTreeNode bArbreToJTree(Noeud<Type> root) {
        StringBuilder txt = new StringBuilder();
        if (root.fils.size() == 0)
        {
            if (previous != null && !reload)
            {
                previous.next = root;
            }
        }
        for (Type key : root.keys)
        {
            txt.append(key.toString()).append(" | ");
        }

        DefaultMutableTreeNode racine2 = new DefaultMutableTreeNode(txt.toString(), true);
        //System.out.println("start print");
        for (Noeud<Type> fil : root.fils){
            racine2.add(bArbreToJTree(fil));
            //System.out.println("le key : " + root.pointeur + " a comme fils : " + fil.pointeur);
        }

        return racine2;
    }

    public boolean search(Type id)
    {
        return search(this.racine, id);
    }

    public boolean search(Noeud<Type> ln, Type id)
    {
        if (ln.fils.size() == 0)
        {
            int found = ln.binarySearch((int)(id));
            if (found == -1) System.out.println("bug = " + ln.keys);
            System.out.println("found it. " + ln.keys.get(found) + "'s ptr is = " + ptrs.get(ln.keys.get(found)));
            return true;
        }
        for (int idx = 0; idx < ln.keys.size(); idx++)
        {
            if ((int)(ln.keys.get(idx)) >= (int)(id))
            {
                return search(ln.fils.get(idx), id);
            }
            else if ((int)(ln.keys.get(idx)) < (int)(id))
            {
                if (ln.keys.size() > idx + 1){
                    if ((int)(ln.keys.get(idx + 1)) >= (int)(id))
                    {
                        return search(ln.fils.get(idx + 1), id);
                    }
                }
                else
                {
                    return search(ln.fils.get(idx + 1), id);
                }
            }
        }
        return false;
    }


    public boolean addValeur(Type valeur) {
        reload = false;
        previous = null;
        System.out.println("Ajout de la valeur : " + valeur.toString());
        if (racine.contient(valeur) == null) {
            Noeud<Type> newRacine = racine.addValeur(valeur);
            if (racine != newRacine)
                racine = newRacine;
            return true;
        }
        return false;
    }

    public boolean addValeur(Type valeur, int pointeur) {
        reload = false;
        previous = null;
        System.out.println("Ajout de la valeur : " + valeur.toString() + " ayant comme pointeur : " + pointeur);
        ptrs.put(valeur, pointeur);
        if (racine.contient(valeur) == null) {
            Noeud<Type> newRacine = racine.addValeur(valeur, pointeur);
            if (racine != newRacine)
                racine = newRacine;
            return true;
        }
        return false;
    }


    public void removeValeur(Type valeur) {
        reload = false;
        previous = null;
        System.out.println("Retrait de la valeur : " + valeur.toString());
        if (racine.contient(valeur) != null) {
            Noeud<Type> newRacine = racine.removeValeur(valeur, false);
            if (racine != newRacine)
                racine = newRacine;
        }
    }
}
