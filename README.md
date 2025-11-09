# Tetris Game

Um jogo clássico de Tetris implementado em Java, com funcionalidades avançadas como sistema de replay, top scores e interface gráfica.

## Descrição

Este projeto é uma implementação completa do jogo Tetris, desenvolvido em Java usando Swing para a interface gráfica. O jogo inclui mecânicas tradicionais como queda de peças, rotação, movimento lateral e eliminação de linhas, além de recursos extras como salvamento de replays e exibição de top scores.

## Funcionalidades

- **Jogo Clássico**: Mecânicas padrão do Tetris, incluindo níveis, pontuação e linhas eliminadas.
- **Sistema de Replay**: Grave e reproduza suas partidas automaticamente.
- **Top Scores**: Visualize as melhores pontuações no painel lateral.
- **Interface Gráfica**: Interface amigável com controles via teclado.
- **Salvamento Persistente**: Scores e replays são salvos em arquivos para acesso futuro.

## Como Executar
 para compilar e executar va no powershell e entre na pasta que está o repositorio
 COmpilar
 ```
mvn -U clean compile
```
Executar
```
mvn exec:java "-Dexec.mainClass=com.tetris.ui.Main" "-Dexec.jvmArgs=-Djava.util.logging.config.file=src/main/resources/res/logging.properties"
```
### Pré-requisitos

- **Java JDK**: Versão 8 ou superior.
- **IDE**: Recomendado usar IntelliJ IDEA, Eclipse ou VS Code com suporte a Java.

### Passos para Execução

1. **Clone o Repositório**:
   ```
   git clone https://github.com/seu-usuario/tetris-game.git
   cd tetris-game
   ```

2. **Compile o Projeto**:
   - Abra o projeto na sua IDE.
   - Compile todas as classes no pacote `com.tetris`.

3. **Execute o Jogo**:
   - Execute a classe principal `Main` (ou a classe que inicia o jogo, como `GamePanel` ou `PlayManager`).
   - O jogo abrirá uma janela gráfica.

4. **Controles**:
   - **Seta Esquerda/Direita**: Mover peça.
   - **Seta Baixo**: Acelerar queda.
   - **Seta Cima**: Rotacionar peça.
   - **Espaço**: Pausar (se implementado).

## Estrutura do Projeto

```
src/main/java/com/tetris/
├── game/
│   ├── PlayManager.java      # Gerencia a lógica principal do jogo
│   └── GamePanel.java        # Painel gráfico do jogo
├── model/
│   ├── Mino.java             # Representa as peças (Tetrominós)
│   ├── Block.java            # Blocos individuais
│   ├── GameAction.java       # Ações para replay
│   └── PlayerScore.java      # Dados de pontuação
├── persistence/
│   └── ScoreRepository.java  # Gerencia salvamento de scores
├── replay/
│   └── ReplayManager.java    # Gerencia salvamento e carregamento de replays
├── ui/
│   └── ReplayDialog.java     # Diálogo para seleção de replays
├── input/
│   └── KeyHandler.java       # Gerencia entrada do teclado
└── Main.java                 # Classe principal para iniciar o jogo
```

## Dependências

- **Java Swing**: Para interface gráfica (incluído no JDK).
- **Java I/O**: Para salvamento de arquivos (incluído no JDK).

Nenhuma dependência externa é necessária.

## Como Jogar

1. Inicie o jogo e digite seu nome de jogador.
2. Use as setas para mover e rotacionar as peças.
3. Elimine linhas completas para ganhar pontos.
4. Quando o jogo acabar, escolha entre recomeçar, ver replays ou sair.
5. No painel lateral, veja seus top scores.

## Sistema de Replay

- As ações do jogador são gravadas automaticamente durante a partida.
- Após o game over, o replay é salvo em um arquivo separado.
- Use o diálogo "Ver Replays" para selecionar e reproduzir uma partida anterior.

## Contribuição

1. Fork o repositório.
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`).
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`).
4. Push para a branch (`git push origin feature/nova-feature`).
5. Abra um Pull Request.


## Autor

Desenvolvido por Marcela Becher. Para dúvidas, entre em contato via marcelabecher0@gmail.com.

---
