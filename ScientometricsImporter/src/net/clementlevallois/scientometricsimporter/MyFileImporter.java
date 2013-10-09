/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.scientometricsimporter;

/*
 Copyright 2008-2013 Clement Levallois
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net


 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2013 Clement Levallois. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s): Clement Levallois

 */
import java.io.IOException;
import javax.swing.DefaultListModel;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.spi.SpigotImporter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;

/**
 * File importer example which can import the Matrix Market file format. This
 * format is a text-based representation of a matrix and can be tested with
 * <a
 * href="http://www2.research.att.com/~yifanhu/GALLERY/GRAPHS/index.html">Yifan
 * Hu's matrix gallery</a>.
 * <p>
 * The example show how graph data should be set in the {@link ContainerLoader}
 * instance. It shows how {@link NodeDraft} and {@link EdgeDraft} are created
 * from the factory. It also append logs in the {@link Report} class, which is
 * the standard way to report messages and issues.
 *
 * @author Mathieu Bastian
 */
public class MyFileImporter implements SpigotImporter, LongTask {

    private ContainerLoader container;
    private Report report;
    private ProgressTicket progressTicket;
    private boolean cancel = false;
    private static String[] headers;
    private static String filePath;
    private static DefaultListModel listModelHeaders = new DefaultListModel();
    private static String textDelimiter = "\"";
    private static String fieldDelimiter = ",";
    private static String firstConnectedAgent;
    private static String secondConnectedAgent;

    @Override
    public boolean execute(ContainerLoader loader) {
        this.container = loader;
        this.report = new Report();
        //Import done here
        return !cancel;
    }

    public static String[] getHeaders() {
        return headers;
    }

    public static void setHeaders(String[] headers) {
        MyFileImporter.headers = headers;
    }

    public static DefaultListModel getListModelHeaders() {
        return listModelHeaders;
    }

    public static void setListModelHeaders(DefaultListModel listModelHeaders) {
        MyFileImporter.listModelHeaders = listModelHeaders;
    }

    public static String getTextDelimiter() {
        return textDelimiter;
    }

    public static void setTextDelimiter(String textDelimiter) {
        MyFileImporter.textDelimiter = textDelimiter;
    }

    public static String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public static void setFieldDelimiter(String fieldDelimiter) {
        MyFileImporter.fieldDelimiter = fieldDelimiter;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        MyFileImporter.filePath = filePath;
    }

    public static String getFirstConnectedAgent() {
        return firstConnectedAgent;
    }

    public static void setFirstConnectedAgent(String firstConnectedAgent) {
        MyFileImporter.firstConnectedAgent = firstConnectedAgent;
    }

    public static String getSecondConnectedAgent() {
        return secondConnectedAgent;
    }

    public static void setSecondConnectedAgent(String secondConnectedAgent) {
        MyFileImporter.secondConnectedAgent = secondConnectedAgent;
    }

    public static void parse() {
        try {
            fieldDelimiter = Panel1.selectedFileDelimiter;
            textDelimiter = Panel1.jTextFieldTextDelimiter.getText();
            CsvParser csvParser = new CsvParser(MyFileImporter.getFilePath(), textDelimiter, fieldDelimiter);
            headers = csvParser.getHeaders();

            MyFileImporter.setHeaders(headers);

            DefaultListModel listModel = new DefaultListModel();
            for (String string : headers) {
                listModel.addElement(string);
            }
            MyFileImporter.setListModelHeaders(listModel);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void getConnectedAgents() {
        firstConnectedAgent = Panel2.firstConnector;
        secondConnectedAgent = Panel2.secondConnector;
    }

    @Override
    public ContainerLoader getContainer() {
        return container;
    }

    @Override
    public Report getReport() {
        return report;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return cancel;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}