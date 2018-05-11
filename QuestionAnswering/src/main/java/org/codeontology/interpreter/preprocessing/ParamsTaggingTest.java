package org.codeontology.interpreter.preprocessing;


public class ParamsTaggingTest {
    public static void main(String[] args) {

        String s = "This sentence contains a \"string parameter\" and another \"string parameter\", and also an 1234 int and 1234 and 1234.5 and 234.6";
        System.out.println(s);
        ParameterizedString p = new ParameterizedString(s);
        System.out.println(p);
        System.out.println(p.get("<double-4>"));
        System.out.println(p.get("<double-5>"));
        System.out.println(p.get("<int-2>"));
        System.out.println(p.get("<int-3>"));
        System.out.println(p.get("<string-0>"));

    }
}
