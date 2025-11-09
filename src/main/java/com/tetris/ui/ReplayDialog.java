package com.tetris.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.tetris.model.PlayerScore;
import com.tetris.persistence.ScoreRepository;
import com.tetris.game.PlayManager;

public class ReplayDialog extends JDialog {
    private final PlayManager playManager;
    private final JList<PlayerScore> scoreList;
    private final DefaultListModel<PlayerScore> listModel;

    public ReplayDialog(JFrame parent, PlayManager playManager) {
        super(parent, "Selecionar Replay", true);
        this.playManager = playManager;

        listModel = new DefaultListModel<>();
        scoreList = new JList<>(listModel);
        scoreList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Layout
        setLayout(new BorderLayout(5, 5));
        add(new JScrollPane(scoreList), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Carrega scores
        refreshScoreList();

        // Config
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        JButton playButton = new JButton("Ver Replay");
        JButton closeButton = new JButton("Fechar");

        playButton.addActionListener(e -> playSelectedReplay());
        closeButton.addActionListener(e -> dispose());

        panel.add(playButton);
        panel.add(closeButton);
        return panel;
    }

    private void refreshScoreList() {
        listModel.clear();
        List<PlayerScore> scores = ScoreRepository.getTopScores(10);
        scores.forEach(listModel::addElement);
    }

    private void playSelectedReplay() {
        PlayerScore selected = scoreList.getSelectedValue();
        if (selected != null) {
            dispose();
            playManager.playReplayFromScore(selected.id);
        }
    }
}