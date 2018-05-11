package org.codeontology.interpreter.evaluation.data;

public class QuestionAnsweringInstance {
    private String question;
    private Object answer;

    public QuestionAnsweringInstance(String question, Object answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public Object getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return "(" + question + ", " + answer + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionAnsweringInstance)) return false;

        QuestionAnsweringInstance that = (QuestionAnsweringInstance) o;

        return (question != null ? question.equals(that.question) : that.question == null) &&
                (answer != null ? answer.equals(that.answer) : that.answer == null);
    }

    @Override
    public int hashCode() {
        int result = question != null ? question.hashCode() : 0;
        result = 31 * result + (answer != null ? answer.hashCode() : 0);
        return result;
    }
}
