package com.mathpar.webWithout.tensors;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mathpar.webWithout.tensors.TensorParser.*;

public class TensorFacade implements TensorPerform {

    @Override
    public String performOperationsOnString(String str) {
        return this.perform(str);
    }

    /**
     *
     * @param latex Строка в форматі Latex.
     * @return Повертає результат обчислень над тензорами в форматі Latex.
     */
    private String perform(String latex) {
        TensorParser parser = new TensorParser();
        // Утворюється список тензорів у строковому представленні
        List<String> tensorsInString = parser.getStringPresentationOfTensors(latex);
        // Асоціація між операцією і тензором
        Map<String, List<Integer>> operations = parser.fetchOperations(latex);
        TensorWorker worker = new TensorWorker();
        Tensor middleResult = null;
        Tensor result = null;
        // Якщо Мапа оперцій пуста, то відбувається згортка, в іншому випадку - відбувається множення, додавання, віднімання
        if (operations.values().stream().allMatch(List::isEmpty)) {
            result = worker.convolutionTensors(parser.buildTensorFromString(tensorsInString.get(0)));
        } else {
            Tensor t1;
            Tensor t2;
            List<Integer> multiplicationIndexes = operations.get(MULTIPLY);
            // Якщо присутня операція множення, то вона виконується першою.
            // Результат записується в проміжне значення middleResult
            if (!CollectionUtils.isEmpty(multiplicationIndexes)) {
                int step = 0;
                for (int i = 0; i < multiplicationIndexes.size() / 2; i++) {
                    t1 = middleResult != null
                            ? middleResult
                            : parser.buildTensorFromString(tensorsInString.get(multiplicationIndexes.get(step)));
                    t2 = parser.buildTensorFromString(tensorsInString.get(multiplicationIndexes.get(step + 1)));
                    middleResult = worker.multiplyTensors(t1, t2);
                    step += 2;
                }
            }
            // Якщо присутнє множення, то операція додавання виконується з результатом множення.
            // В іншому випадку виконується звичайне додавання тензорів.
            List<Integer> addIndexes = operations.get(ADD);
            if (!CollectionUtils.isEmpty(addIndexes)) {
                int step = 0;
                for (int i = 0; i < addIndexes.size() / 2; i++) {
                    if (middleResult != null) {
                        t1 = middleResult;
                        if (!CollectionUtils.isEmpty(multiplicationIndexes)) {
                            List<Integer> notInMultiplication = addIndexes.stream()
                                    .distinct()
                                    .filter(index -> !multiplicationIndexes.contains(index))
                                    .collect(Collectors.toList());
                            t2 = parser.buildTensorFromString(tensorsInString.get(notInMultiplication.get(step)));
                            middleResult = worker.addTensors(t1, t2);
                            step += 1;
                        } else {
                            t2 = parser.buildTensorFromString(tensorsInString.get(addIndexes.get(step + 1)));
                            middleResult = worker.addTensors(t1, t2);
                            step += 2;
                        }
                    } else {
                        t1 = parser.buildTensorFromString(tensorsInString.get(addIndexes.get(step)));
                        t2 = parser.buildTensorFromString(tensorsInString.get(addIndexes.get(step + 1)));
                        middleResult = worker.addTensors(t1, t2);
                        step += 2;
                    }
                }
            }
            // Якщо присутнє множення або додавання, то операція віднімання виконується з їхнім результатом.
            // В іншому випадку виконується звичайне додавання тензорів.
            List<Integer> subtractIndexes = operations.get(SUBTRACT);
            if (!CollectionUtils.isEmpty(subtractIndexes)) {
                int step = 0;
                for (int i = 0; i < subtractIndexes.size() / 2; i++) {
                    if (middleResult != null) {
                        t1 = middleResult;
                        if (!CollectionUtils.isEmpty(multiplicationIndexes) || !CollectionUtils.isEmpty(addIndexes)) {
                            List<Integer> toSubtract = subtractIndexes.stream()
                                    .filter(index -> !Stream.of(multiplicationIndexes, addIndexes)
                                            .flatMap(el -> el.stream())
                                            .distinct()
                                            .collect(Collectors.toList()).contains(index))
                                    .collect(Collectors.toList());
                            t2 = parser.buildTensorFromString(tensorsInString.get(toSubtract.get(step)));
                            middleResult = worker.subtractTensors(t1, t2);
                            step += 1;
                        } else {
                            t2 = parser.buildTensorFromString(tensorsInString.get(subtractIndexes.get(step + 1)));
                            middleResult = worker.subtractTensors(t1, t2);
                            step += 2;
                        }
                    } else {
                        t1 = parser.buildTensorFromString(tensorsInString.get(subtractIndexes.get(step)));
                        t2 = parser.buildTensorFromString(tensorsInString.get(subtractIndexes.get(step + 1)));
                        middleResult = worker.subtractTensors(t1, t2);
                        step += 2;
                    }
                }
            }
            result = middleResult;
        }
        // Результат повертається в Latex представленні тензорів.
        return parser.inLatex(result);
    }
}
