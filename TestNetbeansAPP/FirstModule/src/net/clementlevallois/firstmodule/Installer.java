/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.firstmodule;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        // TODO
    }

    @Override
    public boolean closing() {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                "Do you really want to exit the application?",
                "Exit",
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }
}
