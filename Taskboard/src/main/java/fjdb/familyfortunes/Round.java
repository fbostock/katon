package fjdb.familyfortunes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Round {

    private int year;
    private String question;
    private LinkedHashMap<String, Integer> answers;
    private List<String> answerList;
    public Round(int year, String question, LinkedHashMap<String, Integer> answers) {
        this.year = year;
        this.question = question;
        this.answers = answers;
        answerList = new ArrayList<>(answers.keySet());
    }

    public String getQuestion() {
        return question.toUpperCase();
//        String capitalized = question.substring(0,1).toUpperCase() + question.substring(1);
//        return capitalized;
    }

    public int getYear() {
        return year;
    }

    public int getNumberOfAnswers(){
        return answers.size();
    }

    public List<String> getAnswers() {
        return answerList;
    }

    public String getAnswer(int index) {
        return answerList.get(index);
    }

    public int getScore(int index) {
        return answers.get(answerList.get(index));
    }

    public int getScore(String answer) {
        return answers.get(answer);
    }



}
