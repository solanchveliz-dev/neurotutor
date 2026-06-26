const normalizeText = (value = "") =>
  value
    .toString()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();

export const theoryLessons = [
  {
    moduleKey: "fracciones",
    levelKey: "basico",
    lessons: [
      {
        id: "que-es-una-fraccion",
        title: "¿Qué es una fracción?",
        summary: "Comprende cómo una fracción representa partes iguales de un todo.",
        duration: "5 min",
        content: [
          {
            heading: "Una parte de un todo",
            body:
              "Una fracción sirve para representar una o varias partes iguales de algo completo. Por ejemplo, si una pizza se divide en 4 partes iguales y tomas 1, tienes 1/4 de pizza.",
          },
          {
            heading: "Cómo se lee",
            body:
              "La fracción 1/2 se lee un medio. La fracción 3/4 se lee tres cuartos. Primero se nombra cuántas partes tomas y luego en cuántas partes se dividió el todo.",
          },
        ],
        example: "Si divides una barra en 8 partes iguales y coloreas 3, la fracción coloreada es 3/8.",
        checkpoint: "Una fracción siempre necesita que el todo esté dividido en partes iguales.",
      },
      {
        id: "numerador-y-denominador",
        title: "Numerador y denominador",
        summary: "Identifica qué significa el número de arriba y el número de abajo.",
        duration: "6 min",
        content: [
          {
            heading: "El numerador",
            body:
              "El numerador es el número que está arriba. Indica cuántas partes se toman, se pintan o se usan.",
          },
          {
            heading: "El denominador",
            body:
              "El denominador es el número que está abajo. Indica en cuántas partes iguales se dividió el todo.",
          },
        ],
        example: "En 5/8, el numerador es 5 y el denominador es 8.",
        checkpoint: "Si el denominador cambia, cambia el tamaño de cada parte.",
      },
      {
        id: "fracciones-propias",
        title: "Fracciones propias",
        summary: "Reconoce fracciones menores que un entero.",
        duration: "5 min",
        content: [
          {
            heading: "Menores que uno",
            body:
              "Una fracción propia tiene el numerador menor que el denominador. Eso significa que representa menos de un entero.",
          },
          {
            heading: "Cómo reconocerlas",
            body:
              "Compara los dos números. Si el número de arriba es menor que el de abajo, la fracción es propia.",
          },
        ],
        example: "1/3, 2/5 y 7/10 son fracciones propias.",
        checkpoint: "Toda fracción propia representa una cantidad menor que 1.",
      },
      {
        id: "fracciones-impropias",
        title: "Fracciones impropias",
        summary: "Aprende cuándo una fracción representa un entero o más.",
        duration: "6 min",
        content: [
          {
            heading: "Iguales o mayores que uno",
            body:
              "Una fracción impropia tiene el numerador mayor o igual que el denominador. Puede representar un entero completo o más de un entero.",
          },
          {
            heading: "Relación con enteros",
            body:
              "Cuando el numerador y el denominador son iguales, la fracción vale 1. Por ejemplo, 4/4 es un entero.",
          },
        ],
        example: "5/4, 7/3 y 6/6 son fracciones impropias.",
        checkpoint: "Una fracción impropia puede convertirse en número mixto si es mayor que 1.",
      },
    ],
  },
];

export function getTheoryLessons({ module, level, moduleId, levelId }) {
  const moduleText = normalizeText(`${module?.title ?? ""} ${module?.name ?? ""} ${moduleId ?? ""}`);
  const levelText = normalizeText(
    `${level?.name ?? ""} ${level?.backendTitle ?? ""} ${level?.title ?? ""} ${levelId ?? ""}`
  );

  return (
    theoryLessons.find((group) => {
      const moduleMatches =
        moduleText.includes(group.moduleKey) ||
        normalizeText(module?.title).includes(group.moduleKey);
      const levelMatches =
        levelText.includes(group.levelKey) ||
        normalizeText(level?.name).includes(group.levelKey) ||
        normalizeText(level?.backendTitle).includes(group.levelKey);

      return moduleMatches && levelMatches;
    })?.lessons ?? []
  );
}
