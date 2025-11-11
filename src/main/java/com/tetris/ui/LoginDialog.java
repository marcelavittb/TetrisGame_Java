package com.tetris.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * LoginDialog - solicita nome do jogador com visual moderno.
 * Use: LoginDialog login = new LoginDialog(ownerFrame); String name = login.showDialog();
 */
public class LoginDialog extends JDialog {
    private JTextField nameField;
    private JButton btnEnter;
    private JButton btnCancel;
    private String playerName = null;

    public LoginDialog(Frame owner) {
        super(owner, "Entrar - Tetris", true);
        initUI();
    }

    private void initUI() {
        setSize(420, 220);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(12,12));

        // Top: Title + icon
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(com.tetris.util.Theme.BACKGROUND_DARK);
        top.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel title = new JLabel("Bem-vindo ao Tetris");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        top.add(title, BorderLayout.WEST);

        // icon
        ImageIcon icon = com.tetris.util.IconFactory.defaultDialogIcon();
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            top.add(iconLabel, BorderLayout.EAST);
        }
        add(top, BorderLayout.NORTH);

        // Center: form card
        JPanel card = new JPanel();
        card.setBackground(com.tetris.util.Theme.CARD_DARK);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255,255,255,50)),
                BorderFactory.createEmptyBorder(16,16,16,16)
        ));
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblName = new JLabel("Nome do jogador");
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        card.add(lblName, gbc);

        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        card.add(nameField, gbc);

        JLabel hint = new JLabel("Usado para salvar pontuação e replays");
        hint.setForeground(new Color(200,200,200));
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0.0;
        card.add(hint, gbc);

        add(card, BorderLayout.CENTER);

        // Bottom: buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(com.tetris.util.Theme.BACKGROUND_DARK);
        btnEnter = new JButton("Entrar");
        btnEnter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEnter.setFocusPainted(false);
        btnEnter.setBackground(com.tetris.util.Theme.PRIMARY);
        btnEnter.setForeground(Color.WHITE);

        btnCancel = new JButton("Cancelar");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(new Color(60,70,90));
        btnCancel.setForeground(Color.WHITE);

        bottom.add(btnCancel);
        bottom.add(btnEnter);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        btnEnter.addActionListener(e -> onEnter());
        btnCancel.addActionListener(e -> onCancel());
        nameField.addActionListener(e -> onEnter());

        // Close behavior
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void onEnter() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor informe um nome.", "Nome vazio", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }
        // sanitize and limit length
        name = name.replaceAll("[^\\p{Print}]", "").trim();
        if (name.length() > 20) name = name.substring(0, 20);
        playerName = name;
        dispose();
    }

    private void onCancel() {
        playerName = null;
        dispose();
    }

    /**
     * Mostra o diálogo (bloqueante). Retorna null se cancelado.
     */
    public String showDialog() {
        // ensure the dialog is centered relative to owner and request focus
        SwingUtilities.invokeLater(() -> {
            nameField.requestFocusInWindow();
        });
        setVisible(true);
        return playerName;
    }

    // quick test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog d = new LoginDialog(null);
            String name = d.showDialog();
            System.out.println("Nome: " + name);
            System.exit(0);
        });
    }
}
