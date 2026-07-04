package com.neurotutor.user_service.config;

import com.neurotutor.user_service.model.Modulo;
import com.neurotutor.user_service.model.TheoryLesson;
import com.neurotutor.user_service.repository.ModuloRepository;
import com.neurotutor.user_service.repository.TheoryLessonRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TheoryLessonSeeder implements ApplicationRunner {

    private final ModuloRepository moduloRepository;
    private final TheoryLessonRepository theoryLessonRepository;

    public TheoryLessonSeeder(ModuloRepository moduloRepository,
                              TheoryLessonRepository theoryLessonRepository) {
        this.moduloRepository = moduloRepository;
        this.theoryLessonRepository = theoryLessonRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedLevel(6L, "BASICO", basicLessons());
        seedLevel(7L, "INTERMEDIO", intermediateLessons());
        seedLevel(8L, "AVANZADO", advancedLessons());
        updateExistingLessonThree();
        updateExistingLessonFour();
    }

    /**
     * The public lesson endpoint uses the lesson primary key, not its order inside
     * a module. Keep lesson id 3 synchronized even when it was created before
     * structured web content was introduced.
     */
    private void updateExistingLessonThree() {
        TheoryLesson lesson = theoryLessonRepository.findById(3L).orElse(null);
        if (lesson == null) return;

        LessonSeed seed = basicLessons().get(2);
        lesson.setTitle("Partes de una fracción");
        lesson.setSubtitle("Conoce el numerador y el denominador");
        lesson.setSummary("Cada fracción está formada por dos números. Cada uno tiene un significado muy importante.");
        lesson.setIcon(seed.icon());
        lesson.setContentHtml(seed.contentHtml());
        lesson.setWebContentJson(basicWebContent(3));
        lesson.setOrderNumber(3);
        lesson.setActive(true);
        theoryLessonRepository.saveAndFlush(lesson);
    }

    private void updateExistingLessonFour() {
        TheoryLesson lesson = theoryLessonRepository.findById(4L).orElse(null);
        if (lesson == null) return;

        LessonSeed seed = basicLessons().get(3);
        lesson.setTitle("Fracciones propias e impropias");
        lesson.setSubtitle("Compara el numerador y el denominador");
        lesson.setSummary("Ahora que conoces las partes de una fracción, podemos clasificarlas en propias o impropias.");
        lesson.setIcon(seed.icon());
        lesson.setContentHtml(seed.contentHtml());
        lesson.setWebContentJson(basicWebContent(4));
        lesson.setOrderNumber(4);
        lesson.setActive(true);
        theoryLessonRepository.saveAndFlush(lesson);
    }

    private void seedLevel(Long levelId, String expectedLevel, List<LessonSeed> seeds) {
        Modulo module = moduloRepository.findById(levelId).orElse(null);
        if (module == null || !expectedLevel.equals(module.getNivelRequerido())) return;

        for (int index = 0; index < seeds.size(); index++) {
            int orderNumber = index + 1;
            LessonSeed seed = seeds.get(index);
            TheoryLesson lesson = theoryLessonRepository
                    .findByModuloIdAndOrderNumber(levelId, orderNumber)
                    .orElse(null);

            if (lesson == null) {
                lesson = new TheoryLesson();
                lesson.setModulo(module);
                lesson.setTitle(seed.title());
                lesson.setSubtitle(seed.subtitle());
                lesson.setSummary(seed.summary());
                lesson.setIcon(seed.icon());
                lesson.setContentHtml(seed.contentHtml());
                lesson.setOrderNumber(orderNumber);
                lesson.setActive(true);
                theoryLessonRepository.save(lesson);
            } else if (lesson.getContentHtml() == null || lesson.getContentHtml().isBlank()) {
                lesson.setContentHtml(seed.contentHtml());
                theoryLessonRepository.save(lesson);
            }

            if ("BASICO".equals(expectedLevel)
                    && (orderNumber == 1 || orderNumber == 2 || orderNumber == 3 || orderNumber == 4
                    || lesson.getWebContentJson() == null || lesson.getWebContentJson().isBlank())) {
                lesson.setWebContentJson(basicWebContent(orderNumber));
                theoryLessonRepository.save(lesson);
            }

            if ("BASICO".equals(expectedLevel) && orderNumber == 2) {
                lesson.setTitle("¿Qué es una fracción?");
                lesson.setSubtitle("Fracciones: partes iguales de un todo");
                lesson.setSummary("Una fracción representa una parte o porción de algo que ha sido dividido en partes iguales.");
                theoryLessonRepository.save(lesson);
            } else if ("BASICO".equals(expectedLevel) && orderNumber == 3) {
                lesson.setTitle("Partes de una fracción");
                lesson.setSubtitle("Conoce el numerador y el denominador");
                lesson.setSummary("Cada fracción está formada por dos números. Cada uno tiene un significado muy importante.");
                theoryLessonRepository.save(lesson);
            } else if ("BASICO".equals(expectedLevel) && orderNumber == 4) {
                lesson.setTitle("Fracciones propias e impropias");
                lesson.setSubtitle("Compara el numerador y el denominador");
                lesson.setSummary("Diferencia fracciones propias e impropias mediante ejemplos y comparaciones visuales.");
                theoryLessonRepository.save(lesson);
            } else if ("BASICO".equals(expectedLevel) && orderNumber == 5) {
                lesson.setTitle("Repaso antes de la práctica");
                lesson.setSubtitle("Prepárate para resolver ejercicios");
                lesson.setSummary("Refuerza las ideas principales del nivel antes de comenzar la práctica.");
                theoryLessonRepository.save(lesson);
            }
        }
    }

    private String basicWebContent(int lessonNumber) {
        return switch (lessonNumber) {
            case 1 -> """
                    {
                      "hero":{"badge":"Lección 1","title":"Bienvenida al mundo de las fracciones","subtitle":"🌟 Tu aventura comienza aquí","description":"Descubre cómo usamos fracciones al compartir alimentos y objetos.","image":"lecciones_saludo.png","time":"5 min","points":5},
                      "sections":[
                        {"type":"learning_objectives","title":"Hoy aprenderás","items":["Qué representa una fracción.","Dónde usamos fracciones en la vida diaria.","Cómo repartir objetos en partes iguales.","Qué aprenderás durante este nivel."]},
                        {"type":"important_idea","title":"Idea importante","text":"Las fracciones nos ayudan a representar partes de un todo cuando compartimos, repartimos o medimos algo."},
                        {"type":"neo_tip","title":"NEO recomienda","text":"Antes de seguir, observa ejemplos de tu vida diaria: una pizza, una torta o una barra de chocolate pueden ayudarte a entender mejor.","image":"neo_leccion.png"},
                        {"type":"example","title":"Ejemplo: Pizza dividida","text":"Si compartes una pizza entre varias personas, cada porción representa una parte de la pizza completa.","visual":"pizza.png","items":[{"label":"1/2","description":"La pizza se divide en 2 partes iguales."},{"label":"1/4","description":"La pizza se divide en 4 partes iguales."},{"label":"3/4","description":"Se toman 3 de 4 partes iguales."}]},
                        {"type":"observe","title":"Observa","text":"Cuando compartes una pizza entre dos personas, cada una recibe una parte igual. Si la partes en cuatro, cada parte es más pequeña, pero sigue siendo una fracción del todo.","image":"lecciones_saludo.png"},
                        {"type":"summary","title":"Resumen de la lección","items":["Una fracción representa una parte de un todo.","Las fracciones aparecen cuando repartimos algo en partes iguales.","Puedes encontrar fracciones en alimentos, tiempo, medidas y juegos."]}
                      ],
                      "nextLesson":{"title":"¿Qué es una fracción?","description":"Descubriremos qué es una fracción y cómo se representa."}
                    }
                    """;
            case 2 -> """
                    {
                      "hero":{"badge":"Lección 2","title":"¿Qué es una fracción?","subtitle":"Fracciones: partes iguales de un todo","description":"Una fracción representa una parte o porción de algo que ha sido dividido en partes iguales.","image":"lecciones2.png"},
                      "sections":[
                        {"type":"lesson_objective","title":"Objetivo de la lección","text":"Entender que una fracción representa partes iguales de un todo."},
                        {"type":"definition","title":"¿Qué es una fracción?","text":"Cuando dividimos un objeto o una cantidad en partes iguales, cada parte se puede representar con una fracción."},
                        {"type":"plain_language","title":"En otras palabras...","text":"Una fracción nos dice cuántas partes tomamos de un total de partes iguales.","image":"neo_ideas.png"},
                        {"type":"daily_examples","title":"Ejemplos en la vida diaria","items":[
                          {"title":"Pizza","text":"Si una pizza se divide en 2 partes iguales y tomamos 1 parte, tenemos 1/2 de la pizza.","image":"pizza1.png","numerator":"1","denominator":"2","tone":"amber"},
                          {"title":"Chocolate","text":"Si un chocolate se divide en 4 partes iguales y tomamos 2 partes, tenemos 2/4 del chocolate.","image":"barra_chocolate.png","numerator":"2","denominator":"4","tone":"violet"},
                          {"title":"Torta","text":"Si una torta se divide en 8 partes iguales y tomamos 3 partes, tenemos 3/8 de la torta.","image":"torta.png","numerator":"3","denominator":"8","tone":"rose"}
                        ]},
                        {"type":"simple_representations","title":"Representaciones sencillas","text":"Las fracciones se escriben con dos números separados por una línea.","items":[
                          {"numerator":"1","denominator":"2","caption":"Leemos: un medio","visual":"half"},
                          {"numerator":"2","denominator":"4","caption":"Leemos: dos cuartos","visual":"quarters"}
                        ]},
                        {"type":"important_note","title":"¡Importante!","text":"Cada fracción representa una parte de un todo que ha sido dividido en partes iguales."},
                        {"type":"keep_learning","title":"Sigue aprendiendo","text":"Cada fracción está formada por dos números. En la siguiente lección descubrirás qué significa cada uno.","image":"neo_ideas.png"}
                      ]
                    }
                    """;
            case 3 -> """
                    {
                      "hero":{"badge":"Lección 3","title":"Partes de una fracción","subtitle":"Conoce el numerador y el denominador","description":"Cada fracción está formada por dos números.\nCada uno tiene un significado muy importante.","image":"neo_indicando.png","fraction":{"numerator":"3","denominator":"4"},"numeratorLabel":"Numerador","numeratorText":"Indica cuántas partes se toman.","denominatorLabel":"Denominador","denominatorText":"Indica en cuántas partes iguales se divide el todo."},
                      "sections":[
                        {"type":"fraction_parts","fraction":{"numerator":"3","denominator":"4"},"numerator":{"title":"Numerador","text":"Es el número que está arriba de la línea fraccionaria.","detail":"Nos indica cuántas partes del total estamos tomando."},"denominator":{"title":"Denominador","text":"Es el número que está abajo de la línea fraccionaria.","detail":"Nos indica en cuántas partes iguales se ha dividido el todo."},"example":{"title":"Ejemplo:","text":"En esta pizza hay 4 partes iguales en total y tomamos 3 de ellas.","image":"pizza3.png"}},
                        {"type":"more_examples","title":"Más ejemplos","items":[
                          {"title":"Chocolate","text":"Tomamos 2 de las 4 partes iguales del chocolate.","image":"barra_chocolate.png","numerator":"2","denominator":"4","tone":"violet"},
                          {"title":"Torta","text":"Tomamos 1 de las 2 partes iguales de la torta.","image":"tajada_torta.png","numerator":"1","denominator":"2","tone":"blue"},
                          {"title":"Manzanas","text":"Tomamos 3 de las 5 partes iguales de las manzanas.","image":"manzanas.png","numerator":"3","denominator":"5","tone":"green"}
                        ]},
                        {"type":"comparison_table","title":"Compara y aprende","columns":{"fraction":"Fracción","numerator":"Numerador","denominator":"Denominador","interpretation":"Interpretación"},"rows":[
                          {"numerator":"1","denominator":"2","numeratorText":"Parte que tomamos","denominatorText":"Partes en que se divide el todo","interpretation":"Tomamos 1 de las 2 partes del total."},
                          {"numerator":"2","denominator":"4","numeratorText":"Partes que tomamos","denominatorText":"Partes en que se divide el todo","interpretation":"Tomamos 2 de las 4 partes del total."},
                          {"numerator":"3","denominator":"4","numeratorText":"Partes que tomamos","denominatorText":"Partes en que se divide el todo","interpretation":"Tomamos 3 de las 4 partes del total."}
                        ]},
                        {"type":"remember","title":"¡Recuerda!","text":"El denominador nunca puede ser 0.","detail":"Cada fracción representa una parte de un todo.","image":"neo_recuerda.png"},
                        {"type":"neo_help","title":"NEO te ayuda","text":"Siempre observa primero el denominador para saber en cuántas partes se dividió el todo. Luego mira el numerador para saber cuántas partes se tomaron.","image":"neo_ideas.png"}
                      ]
                    }
                    """;
            case 4 -> """
                    {
                      "hero":{"badge":"Lección 4","title":"Fracciones propias e impropias","subtitle":"Compara el numerador y el denominador","description":"Ahora que conoces las partes de una fracción, podemos clasificarlas en propias o impropias.","image":"neo_indicando.png","comparisons":[
                        {"title":"Propia","numerator":"1","denominator":"4","text":"El numerador es menor que el denominador.","tone":"green"},
                        {"title":"Impropia","numerator":"5","denominator":"3","text":"El numerador es mayor que el denominador.","tone":"orange"}
                      ]},
                      "sections":[
                        {"type":"proper_fractions","title":"1. Fracciones propias","text":"El numerador es menor que el denominador.","items":[
                          {"numerator":"1","denominator":"2","comparison":"1 < 2","result":"Es propia","visual":"circle"},
                          {"numerator":"1","denominator":"4","comparison":"1 < 4","result":"Es propia","visual":"circle"},
                          {"numerator":"3","denominator":"5","comparison":"3 < 5","result":"Es propia","visual":"circle"},
                          {"numerator":"2","denominator":"7","comparison":"2 < 7","result":"Es propia","visual":"circle"}
                        ],"note":{"title":"Recuerda","text":"En las fracciones propias, siempre tomamos menos partes de las que hay en total."}},
                        {"type":"improper_fractions","title":"2. Fracciones impropias","text":"El numerador es mayor que el denominador.","items":[
                          {"numerator":"3","denominator":"2","comparison":"3 > 2","result":"Es impropia","visual":"circle"},
                          {"numerator":"5","denominator":"4","comparison":"5 > 4","result":"Es impropia","visual":"blocks"},
                          {"numerator":"7","denominator":"5","comparison":"7 > 5","result":"Es impropia","visual":"blocks"},
                          {"numerator":"4","denominator":"3","comparison":"4 > 3","result":"Es impropia","visual":"circle"}
                        ],"note":{"title":"Importante","text":"En las fracciones impropias, se toman más partes de las que hay en total."}},
                        {"type":"life_example","title":"Ejemplo en la vida diaria","text":"Si tienes 2 pizzas y divides cada una en 4 partes, en total tienes 8 partes. Si tomas 5 partes, has tomado más de una pizza completa.","numerator":"5","denominator":"4","message":"Es una fracción impropia.","image":"pizza.png","neoImage":"neo_indicando.png"},
                        {"type":"continue_learning","title":"¡Continúa aprendiendo!","text":"Ya sabes diferenciar entre fracciones propias e impropias. En la siguiente lección haremos un repaso antes de la práctica.","image":"neo_ideas.png"}
                      ]
                    }
                    """;
            case 5 -> """
                    {
                      "hero":{"badge":"Lección 5","title":"Repaso antes de la práctica","subtitle":"Estás listo para poner a prueba lo aprendido","description":"Repasa las ideas principales del nivel antes de comenzar los ejercicios.","image":"lecciones_basico5.png"},
                      "sections":[
                        {"type":"main_concept","title":"Resumen de lo aprendido","text":"Las fracciones representan partes iguales de un todo y se interpretan observando sus dos números.","visual":"lecciones_basico5.png"},
                        {"type":"example","title":"Repaso rápido","text":"En 3/4 se toman 3 de 4 partes iguales. Como 3 es menor que 4, es una fracción propia.","visual":"pizza.png"},
                        {"type":"important_idea","title":"Ideas importantes","text":"Las partes deben ser iguales; el numerador va arriba y el denominador abajo; compara ambos para clasificar."},
                        {"type":"neo_tip","title":"NEO dice","text":"Explica cada respuesta con tus propias palabras antes de pasar a la práctica.","image":"neo_leccion.png"},
                        {"type":"learning_objectives","title":"Hoy aprenderás","items":["Recordar qué representa una fracción.","Distinguir numerador y denominador.","Clasificar fracciones propias e impropias.","Prepararte para resolver ejercicios."]},
                        {"type":"common_mistakes","title":"Errores comunes","items":["Olvidar que las partes deben ser iguales.","Intercambiar numerador y denominador.","Pensar que todas las fracciones son menores que uno."]},
                        {"type":"reflection","title":"Antes de continuar","text":"¿Puedes explicar con un ejemplo qué es una fracción y clasificarla? Entonces estás listo para practicar."}
                      ]
                    }
                    """;
            default -> null;
        };
    }

    private List<LessonSeed> basicLessons() {
        return List.of(
                lesson("Bienvenida al mundo de las fracciones", "Tu aventura comienza aquí",
                        "Descubre cómo usamos fracciones al compartir alimentos y objetos.", "👋", """
                        <section class="lesson-lead">
                          <h2>¡Bienvenido al mundo de las fracciones!</h2>
                          <p>Cuando repartes una pizza, una barra de chocolate o una torta en partes iguales, ya estás usando fracciones.</p>
                        </section>
                        <section>
                          <h3>¿Qué aprenderás?</h3>
                          <ul class="lesson-list">
                            <li>Qué representa una fracción.</li>
                            <li>Cómo reconocer numerador y denominador.</li>
                            <li>La diferencia entre fracciones propias e impropias.</li>
                          </ul>
                        </section>
                        <aside class="tip-box"><strong>Idea clave:</strong> las partes deben tener exactamente el mismo tamaño.</aside>
                        """),
                lesson("¿Qué es una fracción?", "Partes iguales de un todo",
                        "Entiende qué es una fracción y cómo se representa.", "🍕", """
                        <section class="lesson-lead">
                          <h2>Una parte de un todo</h2>
                          <p>Una fracción representa una o varias partes iguales de una unidad completa.</p>
                        </section>
                        <div class="math-example"><strong>2/4</strong><span>Dos de cuatro partes iguales</span></div>
                        <section>
                          <h3>Ejemplo</h3>
                          <p>Si una pizza se divide en 4 porciones iguales y tomas 2, has tomado <strong>2/4</strong> de la pizza.</p>
                        </section>
                        <aside class="tip-box"><strong>Recuerda:</strong> si las partes no son iguales, no representan una fracción justa del todo.</aside>
                        """),
                lesson("Partes de una fracción", "Numerador y denominador",
                        "Identifica qué indica el número superior y el número inferior.", "🏛️", """
                        <section class="lesson-lead">
                          <h2>Las partes de una fracción</h2>
                          <p>Una fracción tiene un número arriba, una línea y un número abajo. Cada uno cumple una función.</p>
                        </section>
                        <div class="fraction-parts">
                          <div><strong>3</strong><span>Numerador: partes tomadas</span></div>
                          <div><strong>8</strong><span>Denominador: partes iguales del total</span></div>
                        </div>
                        <p>En <strong>3/8</strong>, el entero fue dividido en 8 partes iguales y se eligieron 3.</p>
                        <aside class="tip-box">El numerador siempre va arriba y el denominador abajo.</aside>
                        """),
                lesson("Fracciones propias", "Cantidades menores que una unidad",
                        "Reconoce fracciones cuyo numerador es menor que el denominador.", "⭐", """
                        <section class="lesson-lead">
                          <h2>Fracciones propias</h2>
                          <p>Una fracción es propia cuando el numerador es menor que el denominador. Su valor es menor que un entero.</p>
                        </section>
                        <div class="concept-grid">
                          <div><strong>2/5</strong><span>2 es menor que 5</span></div>
                          <div><strong>4/7</strong><span>4 es menor que 7</span></div>
                          <div><strong>3/8</strong><span>3 es menor que 8</span></div>
                        </div>
                        <aside class="tip-box"><strong>Regla rápida:</strong> arriba hay un número menor que abajo.</aside>
                        """),
                lesson("Fracciones impropias", "Una unidad o más",
                        "Reconoce fracciones cuyo numerador es mayor o igual que el denominador.", "🚀", """
                        <section class="lesson-lead">
                          <h2>Fracciones impropias</h2>
                          <p>Una fracción impropia tiene el numerador mayor o igual que el denominador. Representa una unidad completa o más.</p>
                        </section>
                        <div class="concept-grid">
                          <div><strong>5/4</strong><span>1 entero y 1/4</span></div>
                          <div><strong>7/3</strong><span>2 enteros y 1/3</span></div>
                          <div><strong>9/8</strong><span>1 entero y 1/8</span></div>
                        </div>
                        <aside class="tip-box">Cuando numerador y denominador son iguales, la fracción vale exactamente 1.</aside>
                        """));
    }

    private List<LessonSeed> intermediateLessons() {
        return List.of(
                lesson("Bienvenida nivel intermedio", "Es momento de operar",
                        "Prepárate para relacionar, sumar, restar, multiplicar y dividir fracciones.", "🚀", """
                        <section class="lesson-lead"><h2>¡Subimos de nivel!</h2><p>Ya conoces las partes de una fracción. Ahora aprenderás a transformarlas y operar con ellas.</p></section>
                        <ul class="lesson-list"><li>Fracciones equivalentes.</li><li>Sumas y restas.</li><li>Multiplicación y división.</li></ul>
                        <aside class="tip-box">Trabaja paso a paso y simplifica siempre que sea posible.</aside>
                        """),
                lesson("Fracciones equivalentes", "Distintos números, mismo valor",
                        "Aprende a obtener fracciones que representan la misma cantidad.", "👯", """
                        <section class="lesson-lead"><h2>Fracciones equivalentes</h2><p>Dos fracciones son equivalentes cuando representan la misma parte del entero.</p></section>
                        <div class="math-example"><strong>1/2 = 2/4 = 4/8</strong><span>Multiplica numerador y denominador por el mismo número.</span></div>
                        <p>También puedes dividir ambos términos por un divisor común para simplificar.</p>
                        <aside class="tip-box">La operación realizada arriba debe ser exactamente la misma que abajo.</aside>
                        """),
                lesson("Suma con igual denominador", "Conserva el denominador",
                        "Suma numeradores cuando las partes tienen el mismo tamaño.", "🍰", """
                        <section class="lesson-lead"><h2>Sumar partes del mismo tamaño</h2><p>Cuando los denominadores son iguales, suma los numeradores y conserva el denominador.</p></section>
                        <div class="math-example"><strong>2/5 + 1/5 = 3/5</strong><span>Dos quintos más un quinto son tres quintos.</span></div>
                        <ol class="lesson-steps"><li>Comprueba que los denominadores sean iguales.</li><li>Suma los numeradores.</li><li>Simplifica si es posible.</li></ol>
                        """),
                lesson("Suma con diferente denominador", "Busca partes del mismo tamaño",
                        "Usa un denominador común antes de sumar.", "🦋", """
                        <section class="lesson-lead"><h2>Un denominador común</h2><p>No podemos sumar partes de distinto tamaño. Primero convertimos las fracciones a equivalentes con un mismo denominador.</p></section>
                        <div class="math-example"><strong>1/2 + 1/3 = 3/6 + 2/6 = 5/6</strong><span>El mínimo común múltiplo de 2 y 3 es 6.</span></div>
                        <aside class="tip-box">Encuentra el MCM, convierte cada fracción y recién entonces suma.</aside>
                        """),
                lesson("Resta de fracciones", "Encuentra la diferencia",
                        "Resta fracciones usando denominadores iguales o equivalentes.", "➖", """
                        <section class="lesson-lead"><h2>Restar fracciones</h2><p>Como en la suma, primero asegúrate de trabajar con denominadores iguales.</p></section>
                        <div class="math-example"><strong>5/7 - 2/7 = 3/7</strong><span>Se conserva el denominador 7.</span></div>
                        <div class="math-example"><strong>3/4 - 1/2 = 3/4 - 2/4 = 1/4</strong><span>Convierte antes de restar.</span></div>
                        """),
                lesson("Multiplicación de fracciones", "Multiplica en línea recta",
                        "Multiplica numeradores y denominadores y simplifica.", "🎯", """
                        <section class="lesson-lead"><h2>Multiplicar fracciones</h2><p>Multiplica numerador por numerador y denominador por denominador.</p></section>
                        <div class="math-example"><strong>2/3 × 4/5 = 8/15</strong><span>El resultado ya está simplificado.</span></div>
                        <aside class="tip-box">Puedes simplificar antes de multiplicar si encuentras factores comunes cruzados.</aside>
                        """),
                lesson("División de fracciones", "Multiplica por el recíproco",
                        "Invierte la segunda fracción y convierte la división en multiplicación.", "🔄", """
                        <section class="lesson-lead"><h2>Dividir fracciones</h2><p>Conserva la primera fracción, invierte la segunda y multiplica.</p></section>
                        <div class="math-example"><strong>3/4 ÷ 2/5 = 3/4 × 5/2 = 15/8</strong><span>15/8 también puede escribirse 1 7/8.</span></div>
                        <ol class="lesson-steps"><li>Invierte el divisor.</li><li>Multiplica.</li><li>Simplifica el resultado.</li></ol>
                        """));
    }

    private List<LessonSeed> advancedLessons() {
        return List.of(
                lesson("Bienvenida nivel avanzado", "Conviértete en experto",
                        "Integra todo lo aprendido para resolver desafíos de varios pasos.", "🏆", """
                        <section class="lesson-lead"><h2>¡Bienvenido al nivel avanzado!</h2><p>Ya dominas las operaciones básicas. Ahora resolverás comparaciones, cantidades y problemas combinados.</p></section>
                        <ul class="lesson-list"><li>Simplificación eficiente.</li><li>Comparación y orden.</li><li>Fracción de una cantidad.</li><li>Operaciones combinadas y problemas reales.</li></ul>
                        """),
                lesson("Simplificación de fracciones", "La forma más simple",
                        "Reduce una fracción dividiendo ambos términos por factores comunes.", "✂️", """
                        <section class="lesson-lead"><h2>Simplificar sin cambiar el valor</h2><p>Divide numerador y denominador por el mismo número hasta que no tengan divisores comunes mayores que 1.</p></section>
                        <div class="math-example"><strong>12/18 ÷ 2 = 6/9 ÷ 3 = 2/3</strong><span>Usar el MCD permite llegar directamente al resultado.</span></div>
                        <aside class="tip-box">La fracción irreducible representa la misma cantidad con números más pequeños.</aside>
                        """),
                lesson("Comparación de fracciones", "Decide cuál es mayor",
                        "Compara y ordena fracciones con estrategias equivalentes.", "⚖️", """
                        <section class="lesson-lead"><h2>Comparar fracciones</h2><p>Si tienen el mismo denominador, compara numeradores. Si tienen distinto denominador, conviértelas o usa productos cruzados.</p></section>
                        <div class="math-example"><strong>3/4 = 9/12 &lt; 10/12 = 5/6</strong><span>Por eso 5/6 es mayor.</span></div>
                        <aside class="tip-box">Para ordenar varias fracciones, un denominador común ayuda a ver todas con la misma escala.</aside>
                        """),
                lesson("Fracción de una cantidad", "Encuentra una parte de un número",
                        "Calcula cuánto representa una fracción dentro de una cantidad total.", "🎯", """
                        <section class="lesson-lead"><h2>Una fracción de una cantidad</h2><p>Divide la cantidad entre el denominador y multiplica el resultado por el numerador.</p></section>
                        <div class="math-example"><strong>3/5 de 60 = 60 ÷ 5 × 3 = 36</strong><span>Una quinta parte vale 12; tres partes valen 36.</span></div>
                        <p>También puedes multiplicar directamente: 60 × 3/5.</p>
                        """),
                lesson("Operaciones combinadas", "Respeta la jerarquía",
                        "Resuelve expresiones con varias operaciones en el orden correcto.", "🧩", """
                        <section class="lesson-lead"><h2>Operaciones combinadas</h2><p>Resuelve primero paréntesis; luego multiplicaciones y divisiones; al final sumas y restas.</p></section>
                        <div class="math-example"><strong>1/2 + (2/3 × 3/4)</strong><span>Primero: 2/3 × 3/4 = 1/2. Después: 1/2 + 1/2 = 1.</span></div>
                        <aside class="tip-box">Escribe un paso por línea para evitar cambiar el orden de las operaciones.</aside>
                        """),
                lesson("Problemas de la vida real", "Modela, resuelve y comprueba",
                        "Aplica fracciones en situaciones cotidianas y explica tu procedimiento.", "🕵️", """
                        <section class="lesson-lead"><h2>Fracciones en situaciones reales</h2><p>Identifica qué representa el total, qué fracción conoces y qué cantidad debes encontrar.</p></section>
                        <div class="problem-box"><strong>Ejemplo:</strong><p>Si 2/3 de un salón son 24 estudiantes, una parte vale 24 ÷ 2 = 12. El total es 12 × 3 = 36 estudiantes.</p></div>
                        <ol class="lesson-steps"><li>Subraya los datos.</li><li>Representa la relación con una fracción.</li><li>Opera y comprueba si la respuesta tiene sentido.</li></ol>
                        """));
    }

    private LessonSeed lesson(String title, String subtitle, String summary,
                              String icon, String contentHtml) {
        return new LessonSeed(title, subtitle, summary, icon, contentHtml);
    }

    private record LessonSeed(String title, String subtitle, String summary,
                              String icon, String contentHtml) {
    }
}
