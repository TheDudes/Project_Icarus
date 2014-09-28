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
 * file: /src/gui/ST_Editor.java
 * vim: foldmethod=syntax:
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ST_Editor extends JFrame
{
    private static final long serialVersionUID = -3233665469366455301L;
    private JComboBox<String> cmb_file_chooser;
    private JScrollPane       scroll_pane;
    private JTextArea         text_area;
    private JButton           btn_save;
    private JButton           btn_restart;
    private JPanel            main_panel;

    public ST_Editor(String[] st_files)
    {
        setTitle("Structure Text File Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(500, 300));
        main_panel = new JPanel();
        setContentPane(main_panel);
        main_panel.setLayout(new BorderLayout(0, 0));

        JPanel panel_top = new JPanel();
        main_panel.add(panel_top, BorderLayout.NORTH);
        panel_top.setLayout(new GridLayout(1, 3, 0, 0));

        cmb_file_chooser = new JComboBox<String>(st_files);
        panel_top.add(cmb_file_chooser);

        btn_restart = new JButton("Restart Icarus");
        panel_top.add(btn_restart);

        btn_save = new JButton("Save File");
        panel_top.add(btn_save);
        btn_save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        btn_restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        cmb_file_chooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        scroll_pane = new JScrollPane();
        main_panel.add(scroll_pane, BorderLayout.CENTER);

        text_area = new JTextArea();
        scroll_pane.setViewportView(text_area);
        text_area.setFont(new Font("Monospaced", Font.PLAIN, 11));
        setVisible(true);
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                try {
                    ST_Editor frame = new ST_Editor(new String[]{"/icarus/test1.st", "/icarus/test2.st"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
