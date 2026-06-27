package com.neurotutor.user_service.config;

import com.neurotutor.user_service.model.Ejercicio;
import com.neurotutor.user_service.model.Modulo;
import com.neurotutor.user_service.repository.EjercicioRepository;
import com.neurotutor.user_service.repository.ModuloRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Component
public class LearningContentSeeder implements ApplicationRunner {

    private final ModuloRepository moduloRepository;
    private final EjercicioRepository ejercicioRepository;

    public LearningContentSeeder(ModuloRepository moduloRepository,
                                 EjercicioRepository ejercicioRepository) {
        this.moduloRepository = moduloRepository;
        this.ejercicioRepository = ejercicioRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        moduloRepository.findAll().stream()
                .filter(this::isFractionModule)
                .filter(module -> "INTERMEDIO".equals(module.getNivelRequerido())
                        || "AVANZADO".equals(module.getNivelRequerido()))
                .forEach(this::seedMissingContent);
    }

    private void seedMissingContent(Modulo module) {
        boolean intermediate = "INTERMEDIO".equals(module.getNivelRequerido());
        if (isBlank(module.getTeoriaHtml())) {
            module.setTeoriaHtml(intermediate ? intermediateTheory() : advancedTheory());
            moduloRepository.save(module);
        }

        List<ExerciseSeed> practice = intermediate ? intermediatePractice() : advancedPractice();
        List<ExerciseSeed> exam = intermediate ? intermediateExam() : advancedExam();
        if (ejercicioRepository.findByModuloIdAndEsExamenFinal(module.getId(), false).isEmpty()) {
            insertExercises(module, practice, false);
        }
        if (ejercicioRepository.findByModuloIdAndEsExamenFinal(module.getId(), true).isEmpty()) {
            insertExercises(module, exam, true);
        }

        if (module.getEjerciciosTotales() <= 0) {
            module.setEjerciciosTotales(practice.size());
            moduloRepository.save(module);
        }
    }

    private void insertExercises(Modulo module, List<ExerciseSeed> seeds, boolean finalExam) {
        for (int index = 0; index < seeds.size(); index++) {
            ExerciseSeed seed = seeds.get(index);
            Ejercicio exercise = new Ejercicio();
            exercise.setModulo(module);
            exercise.setEnunciado(seed.question());
            exercise.setOpciones(seed.options());
            exercise.setRespuestaCorrectaIndex(seed.correctAnswerIndex());
            exercise.setExplicacionTutorIa(seed.explanation());
            exercise.setSubtema(seed.topic());
            exercise.setOrden(index + 1);
            exercise.setPuntos(10);
            exercise.setEsExamenFinal(finalExam);
            ejercicioRepository.save(exercise);
        }
    }

    private boolean isFractionModule(Modulo module) {
        String moduleText = normalize(module.getTitulo());
        String topicText = module.getTema() == null ? "" : normalize(module.getTema().getNombre());
        return moduleText.contains("fraccion") || topicText.contains("fraccion");
    }

    private String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String intermediateTheory() {
        return """
                <article>
                  <h1>Fracciones II: operaciones</h1>
                  <p>En este nivel aprenderás a reconocer fracciones equivalentes, simplificar y resolver operaciones.</p>
                  <h2>Fracciones equivalentes</h2>
                  <p>Multiplica o divide el numerador y el denominador por el mismo número. Por ejemplo, 1/2 = 2/4.</p>
                  <h2>Suma y resta</h2>
                  <p>Si los denominadores son iguales, opera los numeradores. Si son diferentes, busca primero un denominador común.</p>
                  <h2>Multiplicación</h2>
                  <p>Multiplica numeradores entre sí y denominadores entre sí; finalmente simplifica el resultado.</p>
                  <aside><strong>Recuerda:</strong> simplificar no cambia el valor de una fracción.</aside>
                </article>
                """;
    }

    private String advancedTheory() {
        return """
                <article>
                  <h1>Fracciones III: desafíos</h1>
                  <p>Aplicarás fracciones en comparaciones, números mixtos, operaciones combinadas y problemas cotidianos.</p>
                  <h2>Comparación</h2>
                  <p>Usa denominadores comunes o productos cruzados para decidir qué fracción es mayor.</p>
                  <h2>Números mixtos</h2>
                  <p>Para convertir un mixto a impropia, multiplica el entero por el denominador y suma el numerador.</p>
                  <h2>Operaciones combinadas</h2>
                  <p>Resuelve paréntesis, multiplicaciones y divisiones antes que sumas y restas.</p>
                  <aside><strong>Consejo:</strong> simplifica durante el procedimiento para trabajar con números pequeños.</aside>
                </article>
                """;
    }

    private List<ExerciseSeed> intermediatePractice() {
        return List.of(
                seed("¿Qué fracción con denominador 5 es equivalente a 4/10?", List.of("2/5", "4/5", "1/5", "3/5"), 0, "Fracciones equivalentes", "Divide numerador y denominador entre 2: 4/10 = 2/5."),
                seed("¿Cuál es la forma irreducible de 9/12?", List.of("9/4", "3/2", "3/4", "1/4"), 2, "Simplificación", "El MCD de 9 y 12 es 3. Al dividir ambos entre 3 se obtiene 3/4."),
                seed("¿Cuánto es 3/7 + 2/7?", List.of("5/14", "5/7", "1/7", "6/7"), 1, "Suma de fracciones", "Como los denominadores son iguales, se suman los numeradores: 3 + 2 = 5."),
                seed("Calcula 5/12 + 2/12 + 4/12.", List.of("11/36", "11/24", "11/11", "11/12"), 3, "Suma de fracciones", "Se conservan los doceavos y se suman 5 + 2 + 4 = 11."),
                seed("¿Cuál es el resultado de 1/3 + 1/2?", List.of("2/5", "5/6", "2/6", "1/5"), 1, "Denominador común", "El denominador común es 6: 1/3 = 2/6 y 1/2 = 3/6; la suma es 5/6."),
                seed("Calcula 3/4 + 1/8.", List.of("4/12", "4/8", "7/8", "5/8"), 2, "Suma de fracciones", "Convierte 3/4 en 6/8 y suma 1/8 para obtener 7/8."),
                seed("Si usas 2/5 de una caja de cuadernos, ¿qué fracción queda?", List.of("3/5", "2/5", "5/5", "1/5"), 0, "Resta de fracciones", "La caja completa es 5/5. Entonces 5/5 - 2/5 = 3/5."),
                seed("¿Cuál es el resultado de 7/9 - 4/9?", List.of("3/0", "3/18", "1/3", "3/3"), 2, "Resta de fracciones", "Se restan numeradores: 7/9 - 4/9 = 3/9, que se simplifica a 1/3."),
                seed("Una familia come la mitad de los 3/4 de torta que quedaban. ¿Qué fracción comió?", List.of("4/6", "1/2", "3/4", "3/8"), 3, "Fracción de una fracción", "La mitad de 3/4 se calcula multiplicando 1/2 × 3/4 = 3/8."),
                seed("Calcula 2/3 × 4/5.", List.of("6/8", "8/15", "6/15", "2/15"), 1, "Multiplicación", "Multiplica numeradores y denominadores: 2 × 4 = 8 y 3 × 5 = 15."));
    }

    private List<ExerciseSeed> intermediateExam() {
        return List.of(
                seed("¿Cuál es una fracción equivalente a 3/5?", List.of("6/10", "6/8", "9/10", "3/10"), 0, "Equivalencia", "Multiplica numerador y denominador por 2."),
                seed("Simplifica 18/24.", List.of("9/12", "3/4", "6/12", "2/3"), 1, "Simplificación", "Divide 18 y 24 entre su MCD, que es 6."),
                seed("Calcula 4/9 + 2/9.", List.of("6/18", "2/3", "6/9", "2/9"), 1, "Suma", "4/9 + 2/9 = 6/9 = 2/3."),
                seed("Calcula 5/6 - 1/3.", List.of("4/3", "1/2", "4/6", "2/3"), 1, "Resta", "1/3 equivale a 2/6; 5/6 - 2/6 = 3/6 = 1/2."),
                seed("¿Cuál es el resultado de 2/5 + 1/2?", List.of("3/7", "9/10", "3/10", "7/10"), 1, "Denominador común", "2/5 = 4/10 y 1/2 = 5/10; la suma es 9/10."),
                seed("Calcula 3/8 × 4/9.", List.of("12/72", "1/6", "7/17", "3/6"), 1, "Multiplicación", "3/8 × 4/9 = 12/72, que se simplifica a 1/6."),
                seed("¿Qué fracción falta para completar 1 si ya tienes 5/8?", List.of("3/8", "5/8", "2/8", "1/8"), 0, "Complemento", "El entero es 8/8; 8/8 - 5/8 = 3/8."),
                seed("Ordena de menor a mayor: 1/2, 3/4 y 2/3.", List.of("1/2, 2/3, 3/4", "2/3, 1/2, 3/4", "3/4, 2/3, 1/2", "1/2, 3/4, 2/3"), 0, "Comparación", "Con denominador 12 son 6/12, 8/12 y 9/12."),
                seed("¿Cuánto es la tercera parte de 3/5?", List.of("3/15", "1/5", "3/8", "2/5"), 1, "Fracción de una fracción", "1/3 × 3/5 = 3/15 = 1/5."),
                seed("Una receta usa 3/4 de taza y luego 1/8 más. ¿Cuánto usa en total?", List.of("4/12", "7/8", "1/2", "5/8"), 1, "Problemas con fracciones", "3/4 equivale a 6/8; al sumar 1/8 resulta 7/8."));
    }

    private List<ExerciseSeed> advancedPractice() {
        return List.of(
                seed("Compara 3/4 y 5/6. ¿Cuál es mayor?", List.of("3/4", "5/6", "Son iguales", "No se puede saber"), 1, "Comparación", "3/4 = 9/12 y 5/6 = 10/12; por eso 5/6 es mayor."),
                seed("Convierte 3 1/4 a fracción impropia.", List.of("7/4", "12/4", "13/4", "4/13"), 2, "Números mixtos", "3 × 4 + 1 = 13; se conserva el denominador 4."),
                seed("Convierte 17/5 a número mixto.", List.of("3 1/5", "3 2/5", "2 3/5", "5 3/2"), 1, "Números mixtos", "17 ÷ 5 da 3 y residuo 2: 3 2/5."),
                seed("Calcula (1/2 + 1/3) × 3/5.", List.of("5/6", "3/10", "15/30", "1/2"), 3, "Operaciones combinadas", "1/2 + 1/3 = 5/6; 5/6 × 3/5 = 15/30 = 1/2."),
                seed("Calcula 1 - (2/3 × 1/4).", List.of("5/6", "2/12", "1/6", "1/12"), 0, "Operaciones combinadas", "2/3 × 1/4 = 1/6; luego 1 - 1/6 = 5/6."),
                seed("Si 2/5 de los libros son aventuras y 1/3 ciencias, ¿qué fracción queda para historia?", List.of("11/15", "3/15", "4/15", "1/15"), 2, "Problemas", "2/5 + 1/3 = 11/15; el resto es 15/15 - 11/15 = 4/15."),
                seed("Un tercio de los 3/4 de un tanque se consume. ¿Qué fracción del tanque es?", List.of("3/12", "1/4", "5/12", "2/3"), 1, "Fracción de una fracción", "1/3 × 3/4 = 3/12 = 1/4."),
                seed("Si 2/3 de un salón son 16 estudiantes, ¿cuántos estudiantes hay?", List.of("16", "32", "12", "24"), 3, "Proporcionalidad", "Una tercera parte vale 8; las tres partes valen 24."),
                seed("Ariel conserva 1/2 de sus ahorros y gasta 1/4 de esa mitad. ¿Qué fracción conserva?", List.of("3/8", "1/8", "1/2", "1/4"), 0, "Operaciones", "Gasta 1/4 × 1/2 = 1/8; conserva 1/2 - 1/8 = 3/8."),
                seed("Convierte 0,25 a fracción irreducible.", List.of("25/10", "1/4", "2/5", "25/50"), 1, "Decimales y fracciones", "0,25 = 25/100 y al simplificar entre 25 resulta 1/4."));
    }

    private List<ExerciseSeed> advancedExam() {
        return List.of(
                seed("Simplifica 48/120 a su forma irreducible.", List.of("4/10", "2/5", "12/30", "3/5"), 1, "Simplificación", "El MCD es 24; 48/120 = 2/5."),
                seed("Simplifica 45/135.", List.of("1/3", "5/15", "3/9", "1/5"), 0, "Simplificación", "Divide numerador y denominador entre 45."),
                seed("Compara 5/8 y 11/16.", List.of("5/8 es mayor", "Son iguales", "11/16 es mayor", "No se puede saber"), 2, "Comparación", "5/8 = 10/16, por lo que 11/16 es mayor."),
                seed("Ordena de menor a mayor: 2/3, 3/4 y 5/6.", List.of("2/3, 3/4, 5/6", "5/6, 3/4, 2/3", "2/3, 5/6, 3/4", "3/4, 2/3, 5/6"), 0, "Orden de fracciones", "Con denominador 12 equivalen a 8/12, 9/12 y 10/12."),
                seed("Un ciclista completa 3/5 de 60 km. ¿Cuántos kilómetros le faltan?", List.of("36", "40", "12", "24"), 3, "Problemas", "Recorre 36 km y faltan 60 - 36 = 24 km."),
                seed("Elena gasta 2/9 de 4500. ¿Cuánto dinero conserva?", List.of("1000", "3500", "2500", "4000"), 1, "Problemas", "2/9 de 4500 es 1000; conserva 3500."),
                seed("Calcula (3/4 - 1/6) ÷ 7/12.", List.of("7/12", "0", "1", "12/7"), 2, "Operaciones combinadas", "3/4 - 1/6 = 7/12 y dividir 7/12 entre 7/12 da 1."),
                seed("Calcula 2/3 × 1/4 + 5/6.", List.of("1", "1/6", "2/12", "5/12"), 0, "Operaciones combinadas", "2/3 × 1/4 = 1/6; 1/6 + 5/6 = 1."),
                seed("De 2 tortas se consumen 1 3/8. ¿Cuánto queda?", List.of("1 3/8", "5/8", "3/8", "1 5/8"), 1, "Números mixtos", "2 = 16/8 y 1 3/8 = 11/8; quedan 5/8."),
                seed("La mitad del alumnado practica deporte y 2/5 de ese grupo juega baloncesto. ¿Qué fracción del total juega baloncesto?", List.of("2/5", "3/10", "1/2", "1/5"), 3, "Fracción de una fracción", "1/2 × 2/5 = 2/10 = 1/5."));
    }

    private ExerciseSeed seed(String question, List<String> options, int correctAnswerIndex,
                              String topic, String explanation) {
        return new ExerciseSeed(question, options, correctAnswerIndex, topic, explanation);
    }

    private record ExerciseSeed(String question, List<String> options, int correctAnswerIndex,
                                String topic, String explanation) {
    }
}
