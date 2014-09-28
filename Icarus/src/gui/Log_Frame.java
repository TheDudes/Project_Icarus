/**
 * Copyright (c) 2014, HAW-Landshut
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * file: /src/gui/Log_Frame.java
 * vim: foldmethod=syntax:
 */

package gui;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class Log_Frame extends JFrame
{
    private static final long serialVersionUID = -182378403734962120L;
    private JComboBox<String> cmb_verbose;
    private JScrollPane       scroll_pane;
    private JTextArea         field_log;
    private JPanel            main_panel;
    private String[] verbose_lvls = {"verbosity level 0", "verbosity level 1",
                                     "verbosity level 2", "verbosity level 3",
                                     "verbosity level 4"};

    public Log_Frame()
    {
        setTitle("Log");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(500, 300));
        main_panel = new JPanel();
        setContentPane(main_panel);
        main_panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        main_panel.setBorder(new TitledBorder(null, "Logs", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));

        cmb_verbose = new JComboBox<String>(verbose_lvls);
        cmb_verbose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                changed_verbosity();
            }
        });
        main_panel.add(cmb_verbose);

        field_log = new JTextArea();
        field_log.setEditable(false);
        field_log.setFont(new Font("Monospaced", Font.PLAIN, 11));

        scroll_pane = new JScrollPane(field_log);
        scroll_pane.setAutoscrolls(true);
        main_panel.add(scroll_pane);
        setVisible(true);
    }

    private void changed_verbosity()
    {

    }

    public void log(String log)
    {
        field_log.append(log);
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                try {
                    Log_Frame frame = new Log_Frame();
                    for (int i = 0; i < 1000; i++) {
                        frame.log("icarus " + "something went wrong\n");
                        frame.log("configReader " + "something went wrong\n");
                        frame.log("logger " + "something went wrong\n");
                        frame.log("ioManager " + "something went wrong\n");
                        frame.log("parser " + "something went wrong\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
