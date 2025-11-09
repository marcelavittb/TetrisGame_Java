package com.tetris.ui;

import com.tetris.game.PlayManager;
import com.tetris.replay.ReplayManager;
import com.tetris.model.GameAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class ReplayDialog extends JDialog {

    private JList<String> listScores;
    private DefaultListModel<String> listModel;
    private PlayManager playManager;
    private List<String> replayFiles;

    public ReplayDialog(JFrame parent, PlayManager playManager) {
        super(parent, "Selecione uma partida para replay", true);
        this.playManager = playManager;

        listModel = new DefaultListModel<>();
        listScores = new JList<>(listModel);
        listScores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(listScores);

        loadReplayFiles();

        JButton btnReplay = new JButton("Ver Replay");
        btnReplay.addActionListener(e -> playSelectedReplay());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnReplay);
        btnPanel.add(btnCancelar);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }

    private void loadReplayFiles() {
        replayFiles = new ArrayList<>();
        File dir = new File("replays");
        System.out.println("Verificando diretório: " + dir.getAbsolutePath());
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".replay"));
            System.out.println("Arquivos encontrados: " + (files != null ? files.length : 0));
            if (files != null) {
                for (File file : files) {
                    System.out.println("Arquivo: " + file.getName());
                    replayFiles.add(file.getName());
                    listModel.addElement(file.getName().replace(".replay", "").replace("_", " - "));
                }
            }
        } else {
            System.out.println("Diretório 'replays' não existe ou não é um diretório.");
        }
        if (listModel.isEmpty()) {
            listModel.addElement("Nenhum replay salvo");
        }
    }

    private void playSelectedReplay() {
        int idx = listScores.getSelectedIndex();
        if (idx < 0 || idx >= replayFiles.size()) {
            JOptionPane.showMessageDialog(this, "Selecione uma partida válida", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String filename = "replays/" + replayFiles.get(idx);
        String selectedItem = listModel.getElementAt(idx);
        System.out.println("Selecionado na lista: " + selectedItem + " (índice " + idx + ")");
        System.out.println("Arquivo correspondente: " + filename);
        List<GameAction> actions = ReplayManager.loadReplay(filename);
        if (!actions.isEmpty()) {
            System.out.println("Ações carregadas: " + actions.size() + " (primeira ação: " + actions.get(0).actionType + " no frame " + actions.get(0).frameNumber + ")");
            playManager.playReplayFromActions(actions);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao carregar replay.");
        }
        dispose();
    }
}