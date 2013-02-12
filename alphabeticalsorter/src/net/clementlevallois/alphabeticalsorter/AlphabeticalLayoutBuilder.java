package net.clementlevallois.alphabeticalsorter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C. Levallois
 */
import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = LayoutBuilder.class)
public class AlphabeticalLayoutBuilder implements LayoutBuilder {

    private MyLayoutUI ui = new MyLayoutUI();

    public String getName() {
        return NbBundle.getMessage(AlphabeticalLayoutBuilder.class, "Alphabet.name");
    }

    public Layout buildLayout() {
        return new AlphabeticalLayout(this, 20, false);
    }

    public LayoutUI getUI() {
        return ui;
    }

    private static class MyLayoutUI implements LayoutUI {

        public String getDescription() {
            return NbBundle.getMessage(AlphabeticalLayoutBuilder.class, "Alphabet.description");
        }

        public Icon getIcon() {
            return null;
        }

        public JPanel getSimplePanel(Layout layout) {
            return null;
        }

        public int getQualityRank() {
            return -1;
        }

        public int getSpeedRank() {
            return -1;
        }
    }
}