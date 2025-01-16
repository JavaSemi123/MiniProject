package com.sist.client;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.*;
public class ControlPanel extends JPanel{
	HomePanel hp;
	ChatPanel cp;
    CardLayout card=new CardLayout();
   
    public ControlPanel()
    {
    	setLayout(card);
    	hp=new HomePanel(this);
    	add("HOME",hp);
    	
    	cp=new ChatPanel(this);
    	add("CHAT",cp);
    }
	
}
