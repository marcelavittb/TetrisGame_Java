package com.tetris.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.tetris.game.PlayManager;
import com.tetris.replay.ReplayData;
import com.tetris.replay.ReplayManager;
import com.tetris.util.IconFactory;
import com.tetris.util.Theme;

/**
 * Diálogo para listar replays com metadados e preview.
 */
public class ReplayDialogEnhanced extends JDialog {
    private PlayManager playManager;
    private JList<String> list;
    private DefaultListModel<String> model;
    private File[] files;
    private ReplayData[] datas;

    private JTextArea previewArea;

    public ReplayDialogEnhanced(Frame owner, PlayManager pm) {
        super(owner, "Replays", true);
        this.playManager = pm;
        initUI();
    }

    private void initUI() {
        setSize(720, 420);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10,10));

        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellWidth(340);

        refreshList();

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Theme.BACKGROUND_DARK);

        // header with icon
        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setBackground(Theme.CARD_DARK);
        leftHeader.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JLabel headLabel = new JLabel("Replays disponíveis");
        headLabel.setForeground(Color.WHITE);
        headLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        leftHeader.add(headLabel, BorderLayout.WEST);
        ImageIcon icon = IconFactory.getTetrominoIcon(Theme.PRIMARY, 36);
        if (icon != null) leftHeader.add(new JLabel(icon), BorderLayout.EAST);

        leftPanel.add(leftHeader, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(list), BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        previewArea.setBackground(Theme.CARD_DARK);
        previewArea.setForeground(Color.WHITE);
        add(new JScrollPane(previewArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Theme.BACKGROUND_DARK);
        JButton btnLoad = new JButton("Reproduzir");
        btnLoad.setBackground(Theme.PRIMARY);
        btnLoad.setForeground(Color.WHITE);
        JButton btnClose = new JButton("Fechar");
        btnClose.setBackground(new Color(60,70,90));
        btnClose.setForeground(Color.WHITE);
        bottom.add(btnLoad);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        list.addListSelectionListener(e -> onSelect(list.getSelectedIndex()));
        btnLoad.addActionListener(e -> onLoad());
        btnClose.addActionListener(e -> dispose());

        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) onLoad();
            }
        });
    }

    private void refreshList() {
        model.clear();
        File[] raw = ReplayManager.listReplayFiles();
        if (raw == null || raw.length == 0) {
            model.addElement("Nenhum replay encontrado.");
            files = new File[0];
            datas = new ReplayData[0];
            return;
        }
        Arrays.sort(raw, Comparator.comparingLong(File::lastModified).reversed());
        files = new File[raw.length];
        datas = new ReplayData[raw.length];

        for (int i = 0; i < raw.length; i++) {
            File f = raw[i];
            files[i] = f;
            try {
                ReplayData d = ReplayManager.loadReplay(f.getAbsolutePath());
                datas[i] = d;
                String seed = d == null ? "?" : String.valueOf(d.seed);
                int actions = d == null || d.actions == null ? 0 : d.actions.size();
                String score = d == null ? "?" : String.valueOf(d.score);
                model.addElement(String.format("%s  | score:%s  actions:%d  seed:%s", f.getName(), score, actions, seed));
            } catch (Exception ex) {
                datas[i] = null;
                model.addElement(f.getName() + "  | <erro ao ler>");
            }
        }
    }

    private void onSelect(int idx) {
        if (idx < 0 || idx >= datas.length) {
            previewArea.setText("");
            return;
        }
        ReplayData d = datas[idx];
        if (d == null) {
            previewArea.setText("Não foi possível ler metadados deste replay.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Arquivo: ").append(files[idx].getName()).append("\n");
        sb.append("Score: ").append(d.score).append("\n");
        sb.append("Seed: ").append(d.seed).append("\n");
        sb.append("Ações: ").append(d.actions == null ? 0 : d.actions.size()).append("\n\n");
        sb.append("Primeiras ações:\n");
        if (d.actions != null) {
            int take = Math.min(20, d.actions.size());
            for (int i = 0; i < take; i++) {
                sb.append(String.format("%3d: type=%d frame=%d\n", i, d.actions.get(i).actionType, d.actions.get(i).frameNumber));
            }
        }
        previewArea.setText(sb.toString());
        previewArea.setCaretPosition(0);
    }

    private void onLoad() {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= files.length) {
            JOptionPane.showMessageDialog(this, "Selecione um replay.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ReplayData d = datas[idx];
        if (d == null || d.actions == null) {
            JOptionPane.showMessageDialog(this, "Não foi possível carregar este replay.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        playManager.playReplayFromActions(d.actions, d.seed);
        dispose();
    }

    // helper
    public static void open(Frame owner, PlayManager pm) {
        SwingUtilities.invokeLater(() -> {
            ReplayDialogEnhanced d = new ReplayDialogEnhanced(owner, pm);
            d.setVisible(true);
        });
    }
}
