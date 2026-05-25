package agents;

import behaviours.interfacebehaviours.InterfaceAgentBehaviours;
import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import utils.AutoCompleteComboBox;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class InterfaceAgent extends Agent {

    private static final Logger log = Logger.getLogger(InterfaceAgent.class.getName());

    public static final String CONV_USER_REQUEST   = "USER_REQUEST";
    public static final String CONV_RECOMMENDATION = "RECOMMENDATION_RESULT";

    private static final String[] RESTRICTION_OPTIONS = {
            "vegetarian", "vegan", "gluten free", "dairy free"
    };
    private static final String[] PREFERENCE_OPTIONS = {
            "quick", "healthy", "no oven", "light"
    };

    private static final Color C_HEADER_BG   = new Color(32,  78,  58);
    private static final Color C_HEADER_TEXT = Color.WHITE;
    private static final Color C_PAGE_BG     = new Color(245, 248, 246);
    private static final Color C_CARD_BG     = Color.WHITE;
    private static final Color C_ACCENT      = new Color(56, 142, 95);
    private static final Color C_ACCENT_DARK = new Color(32,  78,  58);
    private static final Color C_ACCENT_HOV  = new Color(24,  60,  44);
    private static final Color C_BORDER      = new Color(208, 228, 215);
    private static final Color C_ROW_SEP     = new Color(230, 242, 234);
    private static final Color C_TEXT_PRI    = new Color(28,  40,  34);
    private static final Color C_TEXT_SEC    = new Color(95, 120, 107);
    private static final Color C_STATUS_OK   = new Color(232, 247, 238);
    private static final Color C_STATUS_ERR  = new Color(255, 235, 235);
    private static final Color C_STATUS_WAIT = new Color(255, 248, 225);
    private static final Color C_BTN_REM_BG  = new Color(220,  60,  60);
    private static final Color C_BTN_REM_HOV = new Color(180,  30,  30);

    private static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_SUB     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SECTION = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BOLD12  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_MONO    = new Font("Consolas",  Font.PLAIN, 12);

    JFrame            frame;
    private final java.util.List<Object[]> ingredientRows = new java.util.ArrayList<>();
    JSpinner          spPersons;
    JSpinner          spMaxTime;
    JCheckBox[]       cbxRestrictions = new JCheckBox[RESTRICTION_OPTIONS.length];
    JCheckBox[]       cbxPreferences  = new JCheckBox[PREFERENCE_OPTIONS.length];
    JComboBox<String> cbMealType;
    JButton           btnSearch;
    JEditorPane       taResults;
    JLabel            lblStatus;
    JScrollPane       ingScroll;
    private JPanel    ingredientsPanel;

    @Override
    protected void setup() {
        log.info("InterfaceAgent iniciado: " + getAID().getName());
        registerInDF();
        SwingUtilities.invokeLater(this::buildGUI);
        addBehaviour(new InterfaceAgentBehaviours.WaitForRecommendationBehaviour(this));
    }

    @Override
    protected void takeDown() {
        try { DFService.deregister(this); } catch (FIPAException ignored) {}
        if (frame != null) frame.dispose();
        log.info("InterfaceAgent finalizado.");
    }

    private void registerInDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("interface-agent");
        sd.setName("RecipeInterfaceService");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            log.severe("Error al registrar en el DF: " + e.getMessage());
        }
    }

    public AID findRecipeSearchAgent() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("recipe-search");
        template.addServices(sd);
        try {
            DFAgentDescription[] results = DFService.search(this, template);
            if (results.length > 0) return results[0].getName();
        } catch (FIPAException e) {
            log.warning("Error buscando RecipeSearchAgent: " + e.getMessage());
        }
        return null;
    }

    private void buildGUI() {
        frame = new JFrame("Recipe Recommender");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { doDelete(); }
        });
        frame.setSize(980, 960);
        frame.setMinimumSize(new Dimension(740, 760));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(C_PAGE_BG);
        frame.setLayout(new BorderLayout(0, 0));

        frame.add(buildHeader(),      BorderLayout.NORTH);
        frame.add(buildCenterPanel(), BorderLayout.CENTER);
        frame.add(buildStatusBar(),   BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_HEADER_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JLabel title = new JLabel("<html><span style='font-family:\"Segoe UI Emoji\",\"Segoe UI\";font-size:22px;font-weight:bold;color:white;'>🍽&nbsp;&nbsp;Recipe Recommender</span></html>");
        title.setForeground(C_HEADER_TEXT);

        JLabel sub = new JLabel("Tell us what you have — we'll find recipes that match.");
        sub.setFont(F_SUB);
        sub.setForeground(new Color(185, 220, 200));
        sub.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

        JPanel textBox = new JPanel(new GridLayout(2, 1, 0, 0));
        textBox.setOpaque(false);
        textBox.add(title);
        textBox.add(sub);
        panel.add(textBox, BorderLayout.CENTER);
        return panel;
    }

    private JSplitPane buildCenterPanel() {
        JPanel inputSection = new JPanel();
        inputSection.setLayout(new BoxLayout(inputSection, BoxLayout.Y_AXIS));
        inputSection.setBackground(C_PAGE_BG);
        inputSection.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));
        inputSection.add(buildIngredientCard());
        inputSection.add(Box.createVerticalStrut(10));
        inputSection.add(buildPreferencesCard());
        inputSection.add(Box.createVerticalStrut(12));
        inputSection.add(buildSearchButtonRow());
        inputSection.add(Box.createVerticalStrut(8));

        JScrollPane topScroll = new JScrollPane(inputSection,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        topScroll.setBorder(null);
        topScroll.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                topScroll, buildResultsPanel());
        split.setDividerLocation(480);
        split.setResizeWeight(0.4);
        split.setBorder(null);
        split.setDividerSize(6);
        return split;
    }

    private JPanel buildIngredientCard() {
        JPanel card = card("Ingredients");

        JPanel header = new JPanel(new GridLayout(1, 3, 8, 0));
        header.setBackground(new Color(212, 235, 220));
        header.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        header.add(colHeader("Ingredient  (type to search)"));
        header.add(colHeader("Quantity (grams)"));
        header.add(new JLabel(""));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(header);
        card.add(Box.createVerticalStrut(2));

        ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
        ingredientsPanel.setBackground(C_CARD_BG);

        ingScroll = new JScrollPane(ingredientsPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ingScroll.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        ingScroll.setPreferredSize(new Dimension(0, 165));
        ingScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        ingScroll.getVerticalScrollBar().setUnitIncrement(16);
        card.add(ingScroll);
        card.add(Box.createVerticalStrut(6));

        JButton btnAdd = flatOutlineButton("＋  Add ingredient", C_ACCENT, C_ACCENT);
        btnAdd.addActionListener(e -> {
            addIngredientRow(null, 100);
            ingredientsPanel.revalidate();
            ingredientsPanel.repaint();
            SwingUtilities.invokeLater(() -> {
                JScrollBar sb = ingScroll.getVerticalScrollBar();
                sb.setValue(sb.getMaximum());
            });
        });
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnWrap.add(btnAdd);
        card.add(btnWrap);

        addIngredientRow("rice",    200);
        addIngredientRow("chicken", 300);
        addIngredientRow("egg",      60);
        addIngredientRow("tomato",  150);

        return card;
    }

    private JPanel buildPreferencesCard() {
        JPanel card = card("Cooking preferences");

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row1.setOpaque(false);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        spPersons = spinner(2, 1, 20, 1);
        spMaxTime = spinner(30, 5, 180, 5);
        row1.add(prefLabel("No. of people:"));
        row1.add(spPersons);
        row1.add(Box.createHorizontalStrut(20));
        row1.add(prefLabel("Max cooking time (min):"));
        row1.add(spMaxTime);
        card.add(row1);
        card.add(Box.createVerticalStrut(8));

        card.add(prefLabel("Dietary restrictions:"));
        card.add(Box.createVerticalStrut(3));
        JPanel pRestrictions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        pRestrictions.setOpaque(false);
        pRestrictions.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < RESTRICTION_OPTIONS.length; i++) {
            cbxRestrictions[i] = chipCheckBox(RESTRICTION_OPTIONS[i]);
            pRestrictions.add(cbxRestrictions[i]);
        }
        card.add(pRestrictions);
        card.add(Box.createVerticalStrut(6));

        card.add(prefLabel("Preferences:"));
        card.add(Box.createVerticalStrut(3));
        JPanel pPreferences = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        pPreferences.setOpaque(false);
        pPreferences.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < PREFERENCE_OPTIONS.length; i++) {
            cbxPreferences[i] = chipCheckBox(PREFERENCE_OPTIONS[i]);
            pPreferences.add(cbxPreferences[i]);
        }
        card.add(pPreferences);
        card.add(Box.createVerticalStrut(6));

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbMealType = new JComboBox<>(new String[]{"any", "breakfast", "lunch", "dinner", "snack"});
        cbMealType.setBackground(C_CARD_BG);
        cbMealType.setFont(F_LABEL);
        cbMealType.setPreferredSize(new Dimension(130, 26));
        row2.add(prefLabel("Meal type:"));
        row2.add(cbMealType);
        card.add(row2);

        return card;
    }

    private JPanel buildSearchButtonRow() {
        btnSearch = new JButton("  🔍   Search Recipes  ");
        btnSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSearch.setBackground(C_ACCENT);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setOpaque(true);
        btnSearch.setBorderPainted(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.setPreferredSize(new Dimension(240, 42));
        btnSearch.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, C_ACCENT),
                BorderFactory.createEmptyBorder(6, 20, 6, 20)));
        btnSearch.addActionListener(e -> onSearchClicked());
        btnSearch.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btnSearch.isEnabled()) {
                    btnSearch.setBackground(C_ACCENT_HOV);
                    btnSearch.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(10, C_ACCENT_HOV),
                            BorderFactory.createEmptyBorder(6, 20, 6, 20)));
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btnSearch.isEnabled()) {
                    btnSearch.setBackground(C_ACCENT);
                    btnSearch.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(10, C_ACCENT),
                            BorderFactory.createEmptyBorder(6, 20, 6, 20)));
                }
            }
        });

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        row.setOpaque(false);
        row.add(btnSearch);
        return row;
    }

    private JPanel buildResultsPanel() {
        JPanel resultHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        resultHeader.setBackground(new Color(238, 247, 241));
        resultHeader.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, C_BORDER));
        JLabel resultTitle = new JLabel(
                "<html><span style='font-family:\"Segoe UI Emoji\",\"Segoe UI\";font-size:13px;font-weight:bold;'>"
                + "&#x1F3C6;&nbsp;&nbsp;Recommended Recipes</span></html>");
        resultTitle.setForeground(C_ACCENT_DARK);
        resultHeader.add(resultTitle);

        taResults = new JEditorPane();
        taResults.setEditable(false);
        taResults.setContentType("text/html");
        taResults.setBackground(new Color(252, 255, 253));
        taResults.setText(htmlPage(
                "<p style='color:#5f786b;'><i>Results will appear here after searching…</i></p>"));

        JScrollPane resultsScroll = new JScrollPane(taResults,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultsScroll.setBorder(null);
        resultsScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(C_PAGE_BG);
        wrapper.add(resultHeader,  BorderLayout.NORTH);
        wrapper.add(resultsScroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel buildStatusBar() {
        lblStatus = new JLabel("  Ready  —  Add ingredients and click Search.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setOpaque(true);
        lblStatus.setBackground(C_STATUS_OK);
        lblStatus.setForeground(C_TEXT_SEC);
        lblStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        return lblStatus;
    }

    private void addIngredientRow(String name, int grams) {
        AutoCompleteComboBox cbName = new AutoCompleteComboBox();
        if (name != null && !name.isEmpty()) cbName.setIngredient(name);

        JSpinner spGrams = new JSpinner(new SpinnerNumberModel(
                Math.max(0, grams), 0, 5000, 10));
        spGrams.setFont(F_LABEL);
        ((JSpinner.DefaultEditor) spGrams.getEditor()).getTextField()
                .setHorizontalAlignment(JTextField.LEFT);

        Object[] pair = {cbName, spGrams};
        ingredientRows.add(pair);

        JPanel row = new JPanel(new GridLayout(1, 3, 8, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C_ROW_SEP),
                BorderFactory.createEmptyBorder(5, 6, 5, 6)));
        row.setBackground(C_CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnRemove = new JButton("×");
        btnRemove.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRemove.setBackground(C_BTN_REM_BG);
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setOpaque(true);
        btnRemove.setBorderPainted(false);
        btnRemove.setFocusPainted(false);
        btnRemove.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRemove.setPreferredSize(new Dimension(30, 22));
        btnRemove.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnRemove.setBackground(C_BTN_REM_HOV); }
            @Override public void mouseExited(MouseEvent e)  { btnRemove.setBackground(C_BTN_REM_BG);  }
        });
        btnRemove.addActionListener(e -> {
            ingredientRows.remove(pair);
            ingredientsPanel.remove(row);
            ingredientsPanel.revalidate();
            ingredientsPanel.repaint();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRemove);

        row.add(cbName);
        row.add(spGrams);
        row.add(btnPanel);

        ingredientsPanel.add(row);
    }

    private void onSearchClicked() {
        boolean hasInvalid = ingredientRows.stream()
                .anyMatch(pair -> ((AutoCompleteComboBox) pair[0]).hasInvalidInput());

        if (hasInvalid) {
            JOptionPane.showMessageDialog(frame,
                    "<html><b>Some ingredients are not recognised (shown in red).</b><br>"
                    + "Please select each ingredient from the autocomplete list<br>"
                    + "before searching, or remove the unrecognised rows.</html>",
                    "Invalid ingredients",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean hasIngredient = ingredientRows.stream()
                .anyMatch(pair -> !((AutoCompleteComboBox) pair[0]).getSelectedIngredient().isEmpty());

        if (!hasIngredient) {
            JOptionPane.showMessageDialog(frame,
                    "<html><b>No ingredients added.</b><br>"
                    + "Please add at least one ingredient before searching.</html>",
                    "Ingredient required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnSearch.setEnabled(false);
        btnSearch.setBackground(new Color(100, 160, 120));
        setStatus("⏳  Searching recipes, please wait…", C_STATUS_WAIT);
        taResults.setText(htmlPage("<p style='color:#c47820;'><b>&#9203; Processing request&#8230; please wait.</b></p>"));

        String content = buildRequestContent();
        log.info("Request built:\n" + content);
        addBehaviour(new InterfaceAgentBehaviours.SendRequestBehaviour(this, content));
    }

    String buildRequestContent() {
        StringBuilder ingredients    = new StringBuilder();
        StringBuilder userQuantities = new StringBuilder();

        for (Object[] pair : ingredientRows) {
            AutoCompleteComboBox cb = (AutoCompleteComboBox) pair[0];
            JSpinner             sp = (JSpinner)             pair[1];
            String n = cb.getSelectedIngredient();
            int    g = (Integer) sp.getValue();
            if (!n.isEmpty()) {
                if (ingredients.length() > 0) {
                    ingredients.append(",");
                    userQuantities.append(",");
                }
                ingredients.append(n);
                userQuantities.append(n).append(":").append(g);
            }
        }

        String restrictions = Arrays.stream(cbxRestrictions)
                .filter(JCheckBox::isSelected)
                .map(JCheckBox::getText)
                .collect(Collectors.joining(", "));

        String preferences = Arrays.stream(cbxPreferences)
                .filter(JCheckBox::isSelected)
                .map(JCheckBox::getText)
                .collect(Collectors.joining(", "));

        return "ingredients="    + ingredients             + "\n"
             + "userQuantities=" + userQuantities          + "\n"
             + "persons="        + spPersons.getValue()    + "\n"
             + "maxTime="        + spMaxTime.getValue()    + "\n"
             + "restrictions="   + restrictions            + "\n"
             + "preferences="    + preferences             + "\n"
             + "mealType="       + cbMealType.getSelectedItem();
    }

    public void displayResults(String content) {
        btnSearch.setEnabled(true);
        btnSearch.setBackground(C_ACCENT);
        btnSearch.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, C_ACCENT),
                BorderFactory.createEmptyBorder(6, 20, 6, 20)));

        if (content == null || content.isBlank()) {
            taResults.setText(htmlPage("<p style='color:#b03030;'><b>&#9888; No results received.</b></p>"));
            setStatus("⚠  No results received.", C_STATUS_ERR);
            return;
        }

        java.util.List<java.util.List<String>> groups = new java.util.ArrayList<>();
        java.util.List<String> cur = null;
        for (String line : content.split("\n")) {
            if (line.trim().matches("\\d+\\|.*")) {
                cur = new java.util.ArrayList<>();
                cur.add(line.trim());
                groups.add(cur);
            } else if (cur != null && !line.trim().isEmpty()) {
                cur.add(line.trim());
            }
        }

        if (groups.isEmpty()) {
            taResults.setText(htmlPage("<pre style='font-family:Consolas,monospace;font-size:12px;color:#1c2822;'>"
                    + escapeHtml(content) + "</pre>"));
            taResults.setCaretPosition(0);
            setStatus("⚠  Results received (unformatted).", C_STATUS_WAIT);
            return;
        }

        StringBuilder body = new StringBuilder();

        for (java.util.List<String> group : groups) {
            String header = group.get(0);
            String[] parts = header.split("\\|");
            if (parts.length < 4) {
                body.append("<p><font color='#5f786b' size='2'>").append(escapeHtml(header)).append("</font></p>");
                continue;
            }
            String rank = parts[0];
            String name = parts[1];
            double graphScore, finalScore;
            try {
                graphScore = Double.parseDouble(parts[2]);
                finalScore = Double.parseDouble(parts[3]);
            } catch (NumberFormatException ex) {
                body.append("<p>").append(escapeHtml(header)).append("</p>");
                continue;
            }
            String nutrition5 = parts.length >= 5 ? parts[4].trim() : "";

            String scoreColor  = finalScore >= 0.70 ? "#276d42"
                               : finalScore >= 0.45 ? "#c47820" : "#b03030";
            int barFilled = Math.max(1, (int) Math.round(finalScore * 100));
            int barEmpty  = 100 - barFilled;

            body.append("<table border='0' cellpadding='0' cellspacing='0' width='100%' "
                    + "bgcolor='#ffffff' style='margin-bottom:10px;border:1px solid #d0e4d7;'>");
            body.append("<tr>");

            body.append("<td valign='top' align='center' width='42' bgcolor='#204e3a' "
                    + "style='padding:12px 6px;'>")
                .append("<font color='white' size='3'><b>#").append(rank).append("</b></font>")
                .append("</td>");

            body.append("<td valign='top' style='padding:10px 14px;'>");

            body.append("<b><font size='4' color='#1c2822'>").append(escapeHtml(name)).append("</font></b>");
            body.append("<br><br>");

            body.append("<font size='2' color='#5f786b'>")
                .append("Ingredient match:&nbsp;<b>").append(String.format("%.0f%%", graphScore * 100)).append("</b>")
                .append("&nbsp;&nbsp;&bull;&nbsp;&nbsp;")
                .append("Overall score:&nbsp;<b><font color='").append(scoreColor).append("'>")
                .append(String.format("%.0f%%", finalScore * 100)).append("</font></b>")
                .append("</font>");

            body.append("<br>");
            body.append("<table border='0' cellpadding='0' cellspacing='0' style='margin-top:4px;margin-bottom:6px;'><tr>")
                .append("<td bgcolor='").append(scoreColor).append("' width='").append(barFilled)
                .append("' height='8'></td>")
                .append("<td bgcolor='#e6f2ea' width='").append(barEmpty)
                .append("' height='8'></td>")
                .append("</tr></table>");

            if (!nutrition5.isBlank()) {
                body.append("<font size='2' color='#1c6b3a'><b>&#127822;&nbsp;")
                    .append(escapeHtml(nutrition5)).append("</b></font><br>");
            }

            for (int i = 1; i < group.size(); i++) {
                String det = group.get(i);

                if (det.startsWith("🍎 Nutrition:") && !nutrition5.isBlank()) continue;

                if (det.startsWith("Coverage:")) {
                    body.append("<font size='2' color='#8a9e90'><i>").append(escapeHtml(det)).append("</i></font><br>");
                } else if (det.startsWith("🍎")) {
                    body.append("<font size='2' color='#1c6b3a'><b>").append(escapeHtml(det)).append("</b></font><br>");
                } else if (det.startsWith("✓")) {
                    body.append("<font size='2' color='#276d42'><i>").append(escapeHtml(det)).append("</i></font><br>");
                } else if (det.startsWith("~")) {
                    body.append("<font size='2' color='#c47820'><i>").append(escapeHtml(det)).append("</i></font><br>");
                } else if (det.startsWith("✗") || det.startsWith("⚠")) {
                    body.append("<font size='2' color='#b03030'><i>").append(escapeHtml(det)).append("</i></font><br>");
                } else if (det.startsWith("Ingredients:")) {
                    body.append("<br><font size='2' color='#1c2822'><b>Ingredients:</b></font><br>");
                    body.append("<table width='100%' border='0' cellpadding='1' cellspacing='0' style='margin:2px 0 4px 0;'>");
                    int col = 0;
                    while (i + 1 < group.size() && group.get(i + 1).startsWith("• ")) {
                        i++;
                        if (col == 0) body.append("<tr>");
                        body.append("<td width='280'><font size='2' color='#5f786b'>&bull;&nbsp;")
                            .append(escapeHtml(group.get(i).substring(2)))
                            .append("</font></td>");
                        col++;
                        if (col == 2) { body.append("</tr>"); col = 0; }
                    }
                    if (col == 1) body.append("<td></td></tr>");
                    body.append("</table>");
                } else if (det.startsWith("• ")) {
                    body.append("<font size='2' color='#5f786b'>&nbsp;&nbsp;&bull;&nbsp;")
                        .append(escapeHtml(det.substring(2))).append("</font><br>");
                } else if (det.startsWith("Preparation:")) {
                    String instr = det.substring("Preparation:".length()).trim();
                    body.append("<p style='margin:6px 0 4px 0;'><font size='2' color='#5f786b'>"
                              + "<b>Preparation:</b>&nbsp;")
                        .append(escapeHtml(instr)).append("</font></p>");
                } else {
                    body.append("<font size='2' color='#5f786b'>").append(escapeHtml(det)).append("</font><br>");
                }
            }

            body.append("</td></tr></table>");
        }

        taResults.setText(htmlPage(body.toString()));
        SwingUtilities.invokeLater(() -> {
            try { taResults.setCaretPosition(0); } catch (Exception ignored) {}
        });
        setStatus("✓  " + groups.size() + " recipe(s) recommended.", C_STATUS_OK);
    }

    private String htmlPage(String body) {
        return "<html><head><style>"
            + "body{font-family:'Segoe UI Emoji','Segoe UI',SansSerif;font-size:13px;"
            + "background:#fcfffb;color:#1c2822;margin:14px;padding:0;}"
            + "b{font-weight:bold;}i{font-style:italic;}"
            + "pre{font-family:Consolas,monospace;white-space:pre-wrap;}"
            + "</style></head><body>"
            + body
            + "</body></html>";
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public void setStatus(String msg) {
        setStatus(msg, C_STATUS_OK);
    }

    public void setStatus(String msg, Color bg) {
        lblStatus.setText("  " + msg);
        lblStatus.setBackground(bg != null ? bg : C_STATUS_OK);
    }

    public void enableSearch() {
        btnSearch.setEnabled(true);
        btnSearch.setBackground(C_ACCENT);
    }

    private JPanel card(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(C_CARD_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, C_BORDER),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(F_SECTION);
        lbl.setForeground(C_ACCENT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, C_ACCENT),
                BorderFactory.createEmptyBorder(0, 0, 7, 0)));
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(10));
        return panel;
    }

    private JLabel colHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(C_ACCENT_DARK);
        return lbl;
    }

    private JLabel prefLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_BOLD12);
        lbl.setForeground(C_TEXT_PRI);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JSpinner spinner(int value, int min, int max, int step) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        sp.setFont(F_LABEL);
        sp.setPreferredSize(new Dimension(72, 30));
        return sp;
    }

    private JCheckBox chipCheckBox(String label) {
        JCheckBox cb = new JCheckBox(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(C_ACCENT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                } else {
                    g2.setColor(C_CARD_BG);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(C_BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setForeground(C_TEXT_PRI);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        cb.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        cb.setContentAreaFilled(false);
        cb.addItemListener(e -> {
            cb.setForeground(cb.isSelected() ? Color.WHITE : C_TEXT_PRI);
            cb.repaint();
        });
        cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return cb;
    }

    private JButton flatOutlineButton(String text, Color textColor, Color borderColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setForeground(textColor);
        btn.setBackground(C_CARD_BG);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(240, 250, 244));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(C_CARD_BG);
            }
        });
        return btn;
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int   arc;
        private final Color color;

        RoundedBorder(int arc, Color color) {
            this.arc   = arc;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(arc / 4, arc / 4, arc / 4, arc / 4);
        }
    }
}
