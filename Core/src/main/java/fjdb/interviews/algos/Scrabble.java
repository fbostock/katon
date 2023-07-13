package fjdb.interviews.algos;

import com.google.common.base.Joiner;

import java.util.HashSet;
import java.util.Set;

public class Scrabble {

    public static void main(String[] args) {

        //given 7 tiles, find all permutations.
        //abc : a,b,c,ab,ba,ac,ca,bc,cb,abc,acb,bac,bca,cab,cba - 15 combos

        String word = "abc";

        //zedawdvyyfumwpupuinbdbfndyehircmylbaowuptgmw
        //zedawdvyyfumwpupuinbdbfndyehircmylbaowuptgwm
        Set<String> perm = findPerm(word);
        System.out.println(perm.size());
        System.out.println(Joiner.on(",").join(perm));


    }



    private static Set<String> findPerm(String word) {

        if (word.length() == 1) {
            HashSet<String> words = new HashSet<>();
            words.add(word);
            return words;
        }
        HashSet<String> words = new HashSet<>();

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            Set<String> perm = findPerm(word.substring(0, i) + word.substring(i + 1));
            for (String s : perm) {
                words.add(c + s);
            }
            words.addAll(perm);
        }

        return words;

    }


}
