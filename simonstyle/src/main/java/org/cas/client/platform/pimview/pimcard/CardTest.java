package org.cas.client.platform.pimview.pimcard;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMViewInfo;

public class CardTest extends JFrame {

    /** Creates a new instance of CardTest */
    public CardTest() {
        Container cp = getContentPane();

        PIMDBModel model = new PIMDBModel();

        PIMViewInfo viewInfo =
                (PIMViewInfo) model.getDataSource(ModelCons.VIEW_INFO_DATA, new int[] { ModelCons.CONTACT_APP,
                        ModelCons.CONTACT_ADDRESS_CARD });

        CardInfo info = new CardInfo();
        info.setDisplayIndexes(model.getFieldNameIndex(viewInfo));
        info.setDisplayFieldNames(model.getFieldNames(viewInfo));
        info.setShowEmptyField(false);

        Object[][] allRecord = model.selectRecords(viewInfo);

        JScrollPane scrollPane =
                new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        CardViewPanel center = new CardViewPanel(800, 300, allRecord, info);
        scrollPane.getViewport().add(center);

        scrollPane.addKeyListener(center.getKeyAdapter());
        cp.add(scrollPane, BorderLayout.CENTER);

        cp.add(new CardMatchingPanel(), BorderLayout.EAST);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 300);
        setVisible(true);
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(
            String[] args) {
        new CardTest();
    }

}
