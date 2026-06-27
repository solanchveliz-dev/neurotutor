package com.neurotutor.user_service.config;

import com.neurotutor.user_service.model.DiagnosticQuestion;
import com.neurotutor.user_service.repository.DiagnosticQuestionRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiagnosticQuestionSeeder implements ApplicationRunner {

    private final DiagnosticQuestionRepository diagnosticQuestionRepository;

    public DiagnosticQuestionSeeder(DiagnosticQuestionRepository diagnosticQuestionRepository) {
        this.diagnosticQuestionRepository = diagnosticQuestionRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (diagnosticQuestionRepository.count() != 0) {
            return;
        }

        diagnosticQuestionRepository.saveAll(List.of(
                question(
                        1,
                        "Como parte de una campaña de reciclaje, los estudiantes de secundaria de una escuela recolectaron 1826 botellas de plástico. Ellos recolectaron 478 botellas de plástico menos que los estudiantes de primaria. ¿Cuántas botellas de plástico recolectaron los estudiantes de primaria?",
                        null,
                        null,
                        List.of(
                                "478 botellas de plástico.",
                                "1348 botellas de plástico.",
                                "2294 botellas de plástico.",
                                "2304 botellas de plástico."
                        ),
                        2
                ),
                question(
                        2,
                        "Mariana recibió 8 cajas con latas de pintura para su ferretería. En cada caja, hay media docena de latas de pintura. Ella venderá cada lata a S/20. ¿Cuánto dinero recibirá Mariana por la venta de todas las latas de pintura?",
                        null,
                        null,
                        List.of("S/34", "S/160", "S/960", "S/1920"),
                        2
                ),
                question(
                        3,
                        "Sergio tiene una piscigranja y necesita comprar 1980 kg de alimento balanceado para peces. El tipo de alimento que utiliza para sus peces solo se vende en bolsas de 50 kg. ¿Cuántas bolsas de alimento balanceado debe comprar Sergio?",
                        null,
                        null,
                        List.of("98 bolsas.", "50 bolsas.", "40 bolsas.", "39 bolsas."),
                        2
                ),
                question(
                        4,
                        "En la bandeja, hay frutas. Algunas son naranjas y otras son manzanas. Observa la imagen.",
                        "¿Qué parte del total de frutas de la bandeja son naranjas?",
                        "/diagnostico/pregunta4.png",
                        List.of("9/5", "9/14", "5/14", "1/14"),
                        1
                ),
                question(
                        5,
                        "Abigail tiene dos piezas de soga de diferente longitud sobre una mesa. Observa.",
                        "Ella usó completamente las dos piezas de soga para amarrar unos troncos de su corral de ovejas. ¿Qué longitud de soga usó Abigail en total?",
                        "/diagnostico/pregunta5.png",
                        List.of("1,5 m", "1,8 m", "4,2 m", "15 m"),
                        2
                ),
                question(
                        6,
                        "¿Qué número debe escribirse dentro del recuadro para que se cumpla la igualdad?",
                        null,
                        "/diagnostico/pregunta6.png",
                        List.of("13", "16", "22", "30"),
                        1
                ),
                question(
                        7,
                        "El siguiente gráfico representa el terreno que utilizará Corina para construir un restaurante.",
                        "Corina colocará un cerco en el contorno de todo el terreno. ¿Cuál es la longitud del cerco que colocará Corina?",
                        "/diagnostico/pregunta7.png",
                        List.of("24 m", "27 m", "72 m", "180 m"),
                        2
                ),
                question(
                        8,
                        "Juan vende tres paquetes de mantequilla por S/5. Él elaboró la siguiente tabla para calcular la cantidad de dinero que tendría que cobrar según la cantidad de paquetes que venda.",
                        "Juan vendió una docena y media de paquetes de mantequilla. ¿Cuánto dinero cobrará por esa venta?",
                        "/diagnostico/pregunta8.png",
                        List.of("S/60", "S/30", "S/20", "S/18"),
                        1
                ),
                question(
                        9,
                        "Eloísa preparó 56 bizcochos. Luego, los colocó en 4 cajas con igual cantidad de bizcochos en cada una. Al terminar de guardarlos, le sobraron 8 bizcochos. ¿Cuántos bizcochos colocó en cada caja?",
                        null,
                        null,
                        List.of("12 bizcochos", "14 bizcochos", "16 bizcochos", "22 bizcochos"),
                        2
                ),
                question(
                        10,
                        "Cuatro amigos quieren tomar un vaso de jugo de naranja cada uno, pero tienen diferentes cantidades de dinero. Mario tiene S/5, Eliana tiene S/7, José tiene S/8 y Lucía tiene S/4. Todos están de acuerdo en prestarse dinero entre ellos para que cada uno pueda comprar un vaso de jugo del mismo precio.\n\nEn el cartel de la tienda, se muestran los tamaños y precios de los vasos de jugo de naranja que se pueden comprar. Observa.",
                        "¿Cuál es el mayor precio que podrán pagar los cuatro amigos por cada vaso de jugo de naranja?",
                        "/diagnostico/pregunta10.png",
                        List.of("S/3", "S/4", "S/5", "S/6"),
                        3
                )
        ));
    }

    private DiagnosticQuestion question(
            int orderNumber,
            String textBeforeImage,
            String textAfterImage,
            String imageUrl,
            List<String> options,
            int correctAnswerIndex
    ) {
        DiagnosticQuestion question = new DiagnosticQuestion();
        question.setTextBeforeImage(textBeforeImage);
        question.setTextAfterImage(textAfterImage);
        question.setImageUrl(imageUrl);
        question.setOptions(options);
        question.setCorrectAnswerIndex(correctAnswerIndex);
        question.setTopic(null);
        question.setExplanation(null);
        question.setOrderNumber(orderNumber);
        question.setActive(true);
        return question;
    }
}
