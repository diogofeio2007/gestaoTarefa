CREATE TABLE IF NOT EXISTS categorias (
    id_cat SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS tarefas (
    id_tarefa SERIAL NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    prioridade VARCHAR(10) NOT NULL,
    estado BOOLEAN NOT NULL,
    data_criacao DATE DEFAULT CURRENT_DATE,
    data_limite DATE,
    data_entrega DATE,
    descricao VARCHAR(255),
    id_cat INTEGER NOT NULL,
    CONSTRAINT fk_cat
    FOREIGN KEY (id_cat)
    REFERENCES categorias(id_cat)
    ON DELETE CASCADE
);