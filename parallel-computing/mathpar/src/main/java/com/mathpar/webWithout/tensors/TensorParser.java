package com.mathpar.webWithout.tensors;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TensorParser {

    public static final String ADD = "ADD";
    public static final String SUBTRACT = "SUBTRACT";
    public static final String MULTIPLY = "MULTIPLY";
    public static final String INVALID = "INVALID";

    /**
     * @param input На вхід подається строка у Latex.
     * @return Cписок тензорів в строковому представленні.
     */
    public List<String> getStringPresentationOfTensors(String input) {
        List<String> tensors = new ArrayList<>();
        if (validateString(input)) {
            int markStart = 0;
            boolean wasPowCharacter = false;
            boolean wasUpperCaseCharacter = false;
            for (int i = 0; i < input.length(); i++) {
                char currentChar = input.charAt(i);
                if (isUpperCase(currentChar)) {
                    wasUpperCaseCharacter = true;
                }
                if (currentChar == '^') {
                    wasPowCharacter = true;
                }
                if (isMoreThanOneTensor(currentChar, i, input)
                        || topRightCoefficientsAreFilled(currentChar, wasPowCharacter, wasUpperCaseCharacter, tensors, i, input)
                        || i == input.length() - 1) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = markStart; j <= i; j++) {
                        sb.append(input.charAt(j));
                    }
                    tensors.add(sb.toString());
                    markStart = i + 3;
                    wasPowCharacter = false;
                    wasUpperCaseCharacter = false;
                }
            }
        } else {
            throw new RuntimeException("Latex is invalid!");
        }
        return clean(tensors);
    }


    /**
     *
     * @param currentChar Символ строки
     * @param i Індекс символа в строці
     * @param input Строка у Latex
     * @return Перевірка, чи в строці більше одного тензора
     */
    private boolean isMoreThanOneTensor(char currentChar, int i, String input) {
        return currentChar == ' ' && !nextCharOperation(i, input).equals(INVALID) && !charBeforeWasOperation(i, input);
    }

    /**
     *
     * @param currentChar
     * @param wasPowCharacter
     * @param wasUpperCaseCharacter
     * @param tensors
     * @param i
     * @param input
     * @return Перевірка правих верхніх індексів тензора.
     */
    private boolean topRightCoefficientsAreFilled(char currentChar, boolean wasPowCharacter, boolean wasUpperCaseCharacter,
                                                  List<String> tensors, int i, String input) {
        return currentChar == '}' && wasPowCharacter && wasUpperCaseCharacter && !tensors.isEmpty() && i != input.length() - 1 && input.charAt(i + 1) != '^';
    }

    /**
     *
     * @param list список тензорів у строковому представленні.
     * @return Очистка списку тензорів у строковому представленні.
     */
    private List<String> clean(List<String> list) {
        return list.stream().filter(elem -> !elem.equals("")).collect(Collectors.toList());
    }

    /**
     *
     * @param i Індекс символу
     * @param str Строка
     * @return Визначення оперції на наступному символі строки.
     */
    private String nextCharOperation(int i, String str) {
        int nextChar = i + 1;
        String operation;
        switch (str.charAt(nextChar)) {
            case '+':
                operation = ADD;
                break;
            case '-':
                operation = SUBTRACT;
                break;
            case '*':
                operation = MULTIPLY;
                break;
            default:
                operation = INVALID;
        }
        return operation;
    }

    /**
     *
     * @param latex Строка в Latex.
     * @return Будує об'єкт тензора із строки у форматі Latex.
     */
    public Tensor buildTensorFromString(String latex) {
        Tensor tensor = null;
        int balance = 0;
        boolean wasPowCharacter = false;
        List<Character> leftBottom = new ArrayList<>();
        List<Character> leftTop = new ArrayList<>();
        List<Character> rightBottom = new ArrayList<>();
        List<Character> rightTop = new ArrayList<>();
        for (int i = 0; i < latex.length(); i++) {
            char currentChar = latex.charAt(i);
            if (currentChar == '{') {
                balance += 1;
            }
            if (currentChar == '}') {
                balance -= 1;
            }
            if (currentChar == '^') {
                wasPowCharacter = true;
            }
            if (isUpperCase(currentChar) && leftBottom.isEmpty()) {
                tensor = new Tensor(currentChar);
                setEmptyLeftSide(tensor);
            }
            if (isUpperCase(currentChar) && !leftBottom.isEmpty()) {
                tensor = new Tensor(currentChar);
                tensor.setLeftBottomCoefficients(toFixedCharArray(leftBottom));
                tensor.setLeftTopCoefficients(toFixedCharArray(leftTop));
                wasPowCharacter = false;
            }
            if (isLowerCase(currentChar) && tensor == null && leftTop.isEmpty() && balance > 0 && !wasPowCharacter) {
                leftBottom.add(currentChar);
            }
            if (isLowerCase(currentChar) && tensor == null && balance > 0 && wasPowCharacter) {
                leftTop.add(currentChar);
            }
            if (isLowerCase(currentChar) && balance > 0 && tensor != null && !wasPowCharacter) {
                rightBottom.add(currentChar);
            }
            if (isLowerCase(currentChar) && balance > 0 && tensor != null && wasPowCharacter) {
                rightTop.add(currentChar);
            }
        }
        if (tensor != null) {
            tensor.setRightBottomCoefficients(toFixedCharArray(rightBottom));
            tensor.setRightTopCoefficients(toFixedCharArray(rightTop));
            //Tensor.printTensor(tensor);
        }
        return tensor;
    }

    /**
     *
     * @param characterList список символьних об'єктів.
     * @return Конвертує список символьних об'єктів в масив примітивів.
     */
    private char[] toFixedCharArray(List<Character> characterList) {
        int size = characterList.size();
        char[] result = new char[size];
        for (int i = 0; i < size; i++) {
            result[i] = characterList.get(i);
        }
        return result;
    }

    /**
     * Заповнення пустими значеннями лівих індексів тензора.
     * @param tensor об'єкт тензора.
     */
    private void setEmptyLeftSide(Tensor tensor) {
        tensor.setLeftBottomCoefficients(new char[]{});
        tensor.setLeftTopCoefficients(new char[]{});
    }

    /**
     *
     * @param input Строка в Latex.
     * @return Мапа операцій та список індексів тензорів, над якими ці операції відбуваються.
     */
    public Map<String, List<Integer>> fetchOperations(String input) {
        Map<String, List<Integer>> operationsToTensors = new HashMap<>();
        int firstOperand = 0;
        int secondOperand = 1;
        operationsToTensors.put(MULTIPLY, new ArrayList<>());
        operationsToTensors.put(ADD, new ArrayList<>());
        operationsToTensors.put(SUBTRACT, new ArrayList<>());
        // this shift was made in order not to rewrite existing functions
        for (int i = -1; i < input.length() - 1; i++) {
            String operation = nextCharOperation(i, input);
            if (!operation.equals(INVALID)) {
                operationsToTensors.get(operation).addAll(Arrays.asList(firstOperand, secondOperand));
                firstOperand += 1;
                secondOperand += 1;
            }
        }
        return operationsToTensors;
    }

    /**
     *
     * @param str Строка в Latex.
     * @return Перевірка на правильність запису строки тензора.
     */
    public boolean validateString(String str) {
        int balance = 0;
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (currentChar == '{') {
                balance += 1;
            }
            if (currentChar == '}') {
                balance -= 1;
            }
            if (balance < 0
                    || currentChar == ' ' && !charBeforeWasOperation(i, str) && i != str.length() - 1 && nextCharOperation(i, str).equals(INVALID)
                    || currentChar == ' ' && !charBeforeWasOperation(i, str) && i <= str.length() - 4 && str.charAt(i + 3) != '{' && !isUpperCase(str.charAt(i + 3))) {
                return false;
            }
        }
        return balance == 0;
    }

    /**
     *
     * @param tensor Об'єкт тензора.
     * @return Строкове представлення об'єкта тензора у Latex.
     */
    public String inLatex(Tensor tensor) {
        StringBuilder sb = new StringBuilder();
        sb.append("{}_");
        if (tensor.leftBottomCoefficients != null && tensor.leftBottomCoefficients.length != 0) {
            sb.append(wrapCoefficients(tensor.leftBottomCoefficients));
            sb.append('^');
            sb.append(wrapCoefficients(tensor.leftTopCoefficients));
        }
        sb.append(tensor.name);
        sb.append('_');
        sb.append(wrapCoefficients(tensor.rightBottomCoefficients));
        sb.append('^');
        sb.append(wrapCoefficients(tensor.rightTopCoefficients));
        return sb.toString();
    }

    /**
     *
     * @param coefficients Список індексів.
     * @return Будує строкове представлення для індексів тензора.
     */
    private StringBuilder wrapCoefficients(char[] coefficients) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; i < coefficients.length; i++) {
            sb.append(coefficients[i]);
        }
        sb.append('}');
        return sb;
    }

    /**
     *
     * @param i індекс
     * @param s Строка
     * @return Функція перевірки, чи попередній індекс був операцією.
     */
    private boolean charBeforeWasOperation(int i, String s) {
        char before = s.charAt(i - 1);
        return before == '+' || before == '-' || before == '*';
    }
}
