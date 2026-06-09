export const modulesData = [
  {
    id: "fracciones",
    title: "Fracciones",
    icon: "🍕",
    description: "Aprende a representar, comparar y operar con fracciones.",
    progress: 60,
    status: "En curso",
    unlocked: true,
    active: true,
    levels: [
      {
        id: "fracciones-basico",
        name: "Básico",
        icon: "🌱",
        status: "En curso",
        progress: 60,
        unlocked: true,
        completed: false,
        color: "basic",
        description: "Reconoce fracciones y sus partes.",
        theory: [
          "Una fracción representa una parte de un todo.",
          "El numerador indica cuántas partes se toman.",
          "El denominador indica en cuántas partes iguales se divide el todo."
        ],
        objectives: [
          "Identificar numerador y denominador.",
          "Representar fracciones simples.",
          "Comparar fracciones básicas."
        ]
      },
      {
        id: "fracciones-intermedio",
        name: "Intermedio",
        icon: "🔥",
        status: "Bloqueado",
        progress: 0,
        unlocked: false,
        completed: false,
        color: "intermediate",
        description: "Suma y resta fracciones con distinto denominador.",
        theory: [
          "Para sumar fracciones con distinto denominador, primero se busca un denominador común.",
          "Luego se convierten las fracciones equivalentes y se operan los numeradores."
        ],
        objectives: [
          "Encontrar denominadores comunes.",
          "Resolver sumas de fracciones.",
          "Resolver restas de fracciones."
        ]
      },
      {
        id: "fracciones-avanzado",
        name: "Avanzado",
        icon: "🚀",
        status: "Bloqueado",
        progress: 0,
        unlocked: false,
        completed: false,
        color: "advanced",
        description: "Resuelve problemas aplicados con fracciones.",
        theory: [
          "Las fracciones se usan para resolver situaciones de reparto, medidas y proporciones.",
          "Es importante interpretar bien el enunciado antes de operar."
        ],
        objectives: [
          "Resolver problemas de contexto.",
          "Interpretar resultados.",
          "Aplicar fracciones en situaciones reales."
        ]
      }
    ]
  },
  {
    id: "decimales",
    title: "Decimales",
    icon: "🔢",
    description: "Comprende números decimales y operaciones básicas.",
    progress: 20,
    status: "Disponible",
    unlocked: true,
    active: false,
    levels: [
      {
        id: "decimales-basico",
        name: "Básico",
        icon: "🌱",
        status: "Disponible",
        progress: 20,
        unlocked: true,
        completed: false,
        color: "basic",
        description: "Identifica números decimales en situaciones cotidianas.",
        theory: [
          "Un número decimal representa cantidades menores o mayores que una unidad.",
          "La coma decimal separa la parte entera de la parte decimal."
        ],
        objectives: [
          "Leer números decimales.",
          "Comparar decimales.",
          "Ubicar decimales en una recta numérica."
        ]
      },
      {
        id: "decimales-intermedio",
        name: "Intermedio",
        icon: "🔥",
        status: "Bloqueado",
        progress: 0,
        unlocked: false,
        completed: false,
        color: "intermediate",
        description: "Opera con números decimales.",
        theory: [
          "Para sumar o restar decimales, se alinean las comas.",
          "Luego se opera como con números naturales."
        ],
        objectives: [
          "Sumar decimales.",
          "Restar decimales.",
          "Resolver problemas simples."
        ]
      },
      {
        id: "decimales-avanzado",
        name: "Avanzado",
        icon: "🚀",
        status: "Bloqueado",
        progress: 0,
        unlocked: false,
        completed: false,
        color: "advanced",
        description: "Aplica decimales en problemas de dinero, medidas y porcentajes.",
        theory: [
          "Los decimales aparecen en precios, medidas y resultados exactos.",
          "Comprender su valor permite resolver problemas con mayor precisión."
        ],
        objectives: [
          "Aplicar decimales en compras.",
          "Resolver problemas de medidas.",
          "Relacionar decimales y porcentajes."
        ]
      }
    ]
  },
  {
    id: "porcentajes",
    title: "Porcentajes",
    icon: "📊",
    description: "Interpreta porcentajes y resuelve problemas de descuento o aumento.",
    progress: 0,
    status: "Bloqueado",
    unlocked: false,
    active: false,
    levels: [
      {
        id: "porcentajes-basico",
        name: "Básico",
        icon: "🌱",
        status: "Bloqueado",
        progress: 0,
        unlocked: false,
        completed: false,
        color: "basic",
        description: "Comprende el significado de porcentaje.",
        theory: [
          "Porcentaje significa una cantidad de cada 100.",
          "Por ejemplo, 25% significa 25 de cada 100."
        ],
        objectives: [
          "Reconocer porcentajes.",
          "Representar porcentajes simples.",
          "Relacionar porcentajes con fracciones."
        ]
      },
      {
        id: "porcentajes-intermedio",
        name: "Intermedio",
        icon: "🔥",
        status: "Bloqueado",
        progress: 0,
        unlocked: false,
        completed: false,
        color: "intermediate",
        description: "Calcula porcentajes de una cantidad.",
        theory: [
          "Para hallar un porcentaje de una cantidad, se multiplica la cantidad por el porcentaje convertido a decimal."
        ],
        objectives: [
          "Calcular 10%, 25% y 50%.",
          "Resolver descuentos.",
          "Resolver aumentos simples."
        ]
      },
      {
        id: "porcentajes-avanzado",
        name: "Avanzado",
        icon: "🚀",
        status: "Bloqueado",
        progress: 0,
        unlocked: false,
        completed: false,
        color: "advanced",
        description: "Resuelve problemas de variación porcentual.",
        theory: [
          "La variación porcentual permite comparar cuánto aumenta o disminuye una cantidad."
        ],
        objectives: [
          "Calcular aumentos.",
          "Calcular descuentos.",
          "Interpretar cambios porcentuales."
        ]
      }
    ]
  }
];