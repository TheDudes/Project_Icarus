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
 * file: /src/gui/Test_IO_Frame.java
 * vim: foldmethod=syntax:
 */

package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.SwingConstants;

public class Test_IO_Frame extends JFrame
{
    private static final long serialVersionUID = 3368916586859222342L;
    private JScrollPane       scroll_pane;
    private JTextField        txt_port;
    private JTextArea         field_log;
    private JPanel            main_panel;
    private JLabel            lbl_port;
    private JPanel            panel_top;

    public Test_IO_Frame(int Port)
    {
        setTitle("Test IO Manager");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(500, 300));
        main_panel = new JPanel();
        setContentPane(main_panel);
        main_panel.setLayout(new BorderLayout(0, 0));

        panel_top = new JPanel();
        main_panel.add(panel_top, BorderLayout.NORTH);
        panel_top.setLayout(new GridLayout(1, 3, 0, 0));

        JButton btn_restart = new JButton("Restart");
        panel_top.add(btn_restart);

        lbl_port = new JLabel("Port: ");
        lbl_port.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_top.add(lbl_port);
        lbl_port.setAlignmentX(Component.RIGHT_ALIGNMENT);

        txt_port = new JTextField();
        panel_top.add(txt_port);
        txt_port.setText(new Integer(Port).toString());
        txt_port.setColumns(10);

        scroll_pane = new JScrollPane();
        scroll_pane.setAutoscrolls(true);
        main_panel.add(scroll_pane, BorderLayout.CENTER);

        field_log = new JTextArea();
        field_log.setRows(1);
        scroll_pane.setViewportView(field_log);
        field_log.setColumns(3);
        field_log.setEditable(false);
        field_log.setFont(new Font("Monospaced", Font.PLAIN, 11));
        btn_restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                //not yet implemented
            }
        });
        setVisible(true);
    }

    public void log(String log)
    {
        field_log.append(log);
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                try {
                    Test_IO_Frame frame = new Test_IO_Frame(5123);
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
