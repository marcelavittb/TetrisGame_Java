package com.tetris.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.tetris.game.PlayManager;
import com.tetris.replay.ReplayData;
import com.tetris.replay.ReplayManager;

/**
 * ReplayDialog corrigido: garante mapeamento 1:1 entre lista exibida e arrays mappedFiles/replayDatas.
 */
public class ReplayDialog extends JDialog {

    private PlayManager playManager;
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private File[] mappedFiles;       // mapeia índice p/ File
    private ReplayData[] replayDatas; // mapeia índice p/ ReplayData

    public ReplayDialog(Frame owner, PlayManager playManager) {
        super(owner, "Replays disponíveis", true);
        this.playManager = playManager;
        initUI();
    }

    private void initUI() {
        setSize(600, 420);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        refreshList();

        add(new JScrollPane(fileList), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton loadBtn = new JButton("Carregar Replay");
        JButton refreshBtn = new JButton("Atualizar lista");
        JButton closeBtn = new JButton("Fechar");
        bottom.add(loadBtn);
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        add(bottom, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> onLoad());
        refreshBtn.addActionListener(e -> refreshList());
        closeBtn.addActionListener(e -> dispose());

        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    onLoad();
                }
            }
        });
    }

    private void onLoad() {
        int idx = fileList.getSelectedIndex();
        if (idx < 0 || mappedFiles == null || idx >= mappedFiles.length) {
            JOptionPane.showMessageDialog(this, "Selecione um arquivo de replay.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        File selected = mappedFiles[idx];
        ReplayData data = replayDatas[idx];

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Arquivo inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (data == null) {
            // tentativa de recarregar (fallback)
            data = ReplayManager.loadReplay(selected.getAbsolutePath());
            replayDatas[idx] = data;
        }

        if (data != null && data.actions != null) {
            // Debug: imprime informações no console para confirmar arquivo e seed
            System.out.println("Carregando arquivo: " + selected.getAbsolutePath());
            System.out.println("score=" + data.score + " actions=" + data.actions.size() + " seed=" + data.seed);

            // Inicia replay no PlayManager com seed
            playManager.playReplayFromActions(data.actions, data.seed);
            JOptionPane.showMessageDialog(this, "Replay carregado. Feche este diálogo para ver o replay.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao carregar o replay.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshList() {
        listModel.clear();

        File[] rawFiles = ReplayManager.listReplayFiles();
        if (rawFiles == null || rawFiles.length == 0) {
            listModel.addElement("Nenhum replay encontrado.");
            fileList.setEnabled(false);
            mappedFiles = new File[0];
            replayDatas = new ReplayData[0];
            return;
        }

        // Ordena por última modificação (mais recentes primeiro) para facilitar
        Arrays.sort(rawFiles, Comparator.comparingLong(File::lastModified).reversed());

        // Vamos construir listas dinâmicas (sem lidar com "added" e arrays parcialmente preenchidos)
        java.util.List<File> mfList = new java.util.ArrayList<>();
        java.util.List<ReplayData> rdList = new java.util.ArrayList<>();

        for (File f : rawFiles) {
            try {
                ReplayData d = ReplayManager.loadReplay(f.getAbsolutePath());
                String seedStr = (d == null ? "?" : String.valueOf(d.seed));
                int actions = (d == null || d.actions == null ? 0 : d.actions.size());
                String score = (d == null ? "?" : String.valueOf(d.score));
                String line = String.format("%s  |  score:%s  actions:%d  seed:%s", f.getName(), score, actions, seedStr);
                listModel.addElement(line);
                mfList.add(f);
                rdList.add(d);
            } catch (Exception ex) {
                // Em caso de erro ao ler, ainda adicionamos a linha e mapeamos o arquivo (com ReplayData null)
                String line = String.format("%s  |  <erro ao ler>", f.getName());
                listModel.addElement(line);
                mfList.add(f);
                rdList.add(null);
            }
        }

        // Converte para arrays para uso no onLoad()
        mappedFiles = mfList.toArray(new File[0]);
        replayDatas = rdList.toArray(new ReplayData[0]);

        fileList.setEnabled(true);
    }
}
