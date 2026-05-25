package utils;

import behaviours.ontologybehaviours.FoodOntology;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Campo de texto con sugerencias emergentes tomadas de FoodOntology.
 * Muestra un borde verde cuando el valor se reconoce y rojo cuando no.
 * getSelectedIngredient() devuelve "" para valores no reconocidos, por lo que se ignoran automáticamente.
 */
public class AutoCompleteComboBox extends JPanel {

    private static final Color COLOR_VALID   = new Color(46, 139, 87);
    private static final Color COLOR_INVALID = new Color(210, 60, 60);

    private final JTextField              textField;
    private final JPopupMenu             popup;
    private final DefaultListModel<String> listModel;
    private final JList<String>          suggestionList;
    private final List<String>           allIngredients;

    private boolean confirmedFromList = false;
    private final Border defaultBorder;

    public AutoCompleteComboBox() {
        setLayout(new BorderLayout());
        allIngredients = new ArrayList<>();

        textField = new JTextField();
        textField.setFont(textField.getFont().deriveFont(12f));
        defaultBorder = textField.getBorder();

        listModel      = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        suggestionList.setBackground(new Color(250, 255, 250));
        suggestionList.setSelectionBackground(new Color(46, 139, 87));
        suggestionList.setSelectionForeground(Color.WHITE);

        popup = new JPopupMenu();
        popup.setFocusable(false);
        popup.setBorder(BorderFactory.createLineBorder(new Color(46, 139, 87), 1));
        JScrollPane scroll = new JScrollPane(suggestionList);
        scroll.setPreferredSize(new Dimension(300, 150));
        scroll.setBorder(null);
        popup.add(scroll);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { onTextChanged(); }
            @Override public void removeUpdate(DocumentEvent e)  { onTextChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { onTextChanged(); }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { selectSuggestion(); }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    suggestionList.requestFocus();
                    if (suggestionList.getModel().getSize() > 0)
                        suggestionList.setSelectedIndex(0);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popup.setVisible(false);
                }
            }
        });

        suggestionList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectSuggestion();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popup.setVisible(false);
                    textField.requestFocus();
                }
            }
        });

        add(textField, BorderLayout.CENTER);

        new Thread(() -> {
            List<String> loaded = FoodOntology.getIngredients();
            SwingUtilities.invokeLater(() -> {
                allIngredients.addAll(loaded);
                updateValidity();
            });
        }, "ontology-loader").start();
    }

    private void onTextChanged() {
        confirmedFromList = false;
        updateSuggestions();
        updateValidity();
    }

    private void updateSuggestions() {
        String query = textField.getText().trim().toLowerCase();
        listModel.clear();

        if (query.length() < 2) {
            popup.setVisible(false);
            return;
        }

        List<String> matches = allIngredients.stream()
                .filter(i -> i.contains(query))
                .limit(10)
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            popup.setVisible(false);
            return;
        }

        matches.forEach(listModel::addElement);
        if (!popup.isVisible())
            popup.show(textField, 0, textField.getHeight());
        textField.requestFocus();
    }

    private void selectSuggestion() {
        String selected = suggestionList.getSelectedValue();
        if (selected != null) {
            confirmedFromList = true;
            textField.setText(selected);
            popup.setVisible(false);
            textField.requestFocus();
            updateValidity();
        }
    }

    private void updateValidity() {
        String text = textField.getText().trim();
        if (text.isEmpty() || allIngredients.isEmpty()) {
            textField.setBorder(defaultBorder);
            return;
        }
        boolean valid = isKnownIngredient(text);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(valid ? COLOR_VALID : COLOR_INVALID, 2, true),
                BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        if (!valid) {
            textField.setToolTipText("Unknown ingredient. Please select one from the list.");
        } else {
            textField.setToolTipText(null);
        }
    }

    private boolean isKnownIngredient(String text) {
        if (confirmedFromList) return true;
        String lower = text.trim().toLowerCase();
        return allIngredients.stream().anyMatch(i -> i.equalsIgnoreCase(lower));
    }

    public boolean hasInvalidInput() {
        String text = textField.getText().trim();
        if (text.isEmpty() || allIngredients.isEmpty()) return false;
        return !isKnownIngredient(text);
    }

    public String getSelectedIngredient() {
        String text = textField.getText().trim();
        if (text.isEmpty()) return "";
        if (allIngredients.isEmpty()) return text; // ontology still loading, accept anything
        return isKnownIngredient(text) ? text : "";
    }

    public void setIngredient(String name) {
        confirmedFromList = true;
        textField.setText(name != null ? name : "");
        updateValidity();
    }
}
