import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

public class Main extends JApplet{
	//웹에서 작동을 하기 위하여 Applet 으로 만듬
	private void reflash(JComboBox<String> combo,HashSet<String> value){
		combo.removeAllItems();
		for(String str : value){
			combo.addItem(str);
		}
	}
	
	public void init(){
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		HashSet<String> deviceNames=new HashSet<String>();
		HashSet<String> jobNames=new HashSet<String>();
		HashSet<String> nodeNames=new HashSet<String>();
		HashMap<String,Device> deviceMap=new HashMap<String,Device>();
		HashMap<String,Node> nodeMap=new HashMap<String,Node>();
		HashMap<String,Job> jobMap=new HashMap<String,Job>();
		
		HashSet<String> nodeToNodeConnects=new HashSet<String>();
		HashSet<String> nodeToDeviceConnects=new HashSet<String>();
		HashMap<String,JComponent> paintNodeToNodeLineMap=new HashMap<String,JComponent>();
		HashMap<String,JComponent> paintNodeToDeviceLineMap=new HashMap<String,JComponent>();
		
		HashMap<String,JComponent> paintNodeMap=new HashMap<String,JComponent>();
		HashMap<String,JComponent> paintDeviceMap=new HashMap<String,JComponent>();
		HashMap<Device,JComponent> paintSelectDeviceMap=new HashMap<Device,JComponent>();
		HashMap<String,Boolean> showNodeMap=new HashMap<String,Boolean>();
		HashMap<String,Boolean> showDeviceMap=new HashMap<String,Boolean>();
		JComboBox<String> cbNodeConnectLayoutNode1=new JComboBox<String>();
		JComboBox<String> cbNodeConnectLayoutNode2=new JComboBox<String>();
		JComboBox<String> cbNodeDeviceConnectLayoutNode=new JComboBox<String>();
		JComboBox<String> cbNodeDeviceConnectDevice=new JComboBox<String>();
		
		this.setLayout(new BorderLayout());
		
		{
			//Map layout
			JPanel panel = new JPanel(new BorderLayout());
			JPanel paintPanel = new JPanel();
			paintPanel.setLayout(null);
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			JButton btnControlNode=new JButton("Control Node");
			JButton btnControlDevice=new JButton("Control Device");
			JButton btnRequestJob=new JButton("Request Job");
			
			btnRequestJob.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub

					JDialog dialog=new JDialog();
					dialog.setResizable(false);
					dialog.setTitle("Request job");
					
					dialog.setLayout(new BorderLayout());
					{
						JPanel centerLayout=new JPanel(new GridLayout(0, 2));
						centerLayout.add(new JLabel("request device"));
						JComboBox<String> cbDevice = new JComboBox<String>();
						for(String node : deviceNames){
							cbDevice.addItem(node);
						}
						centerLayout.add(cbDevice);
						centerLayout.add(new JLabel("request job"));	
						JComboBox<String> cbJob = new JComboBox<String>();
						for(String job : jobNames){
							cbJob.addItem(job);
						}
						centerLayout.add(cbJob);
						centerLayout.add(new JLabel("select color"));
						String[] colos={"Red","Purple","Blue","Green","Orange","Yellow"};
						JComboBox<String> cbColor = new JComboBox<String>();
						for(String c : colos){
							cbColor.addItem(c);
						}
						centerLayout.add(cbColor);
						dialog.add(centerLayout,BorderLayout.CENTER);
						JPanel panel = new JPanel();
						JButton btnRequest = new JButton("Request");
						panel.add(btnRequest);
						dialog.add(panel, BorderLayout.SOUTH);
						btnRequest.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								final Color color;
								String strColor=(String) cbColor.getSelectedItem();
								if(strColor.equals("Purple")){
									color=new Color(0x551a8b);
								}else if(strColor.equals("Blue")){
									color=Color.BLUE;
								}else if(strColor.equals("Green")){
									color=Color.GREEN;
								}else if(strColor.equals("Orange")){
									color=Color.ORANGE;
								}else if(strColor.equals("Yellow")){
									color=Color.YELLOW;
								}else{
									color=Color.RED;
								}
								String jobName = (String) cbJob.getSelectedItem();
								if(jobName.equals("")){
									JOptionPane.showMessageDialog(Main.this, "Input job","Error",JOptionPane.ERROR_MESSAGE);
									return;
								}
								Job job = jobMap.get(jobName);
								if(job==null){
									JOptionPane.showMessageDialog(Main.this, "Input job","Error",JOptionPane.ERROR_MESSAGE);
									return;
								}
								String strDevice=(String)cbDevice.getSelectedItem();
								if(strDevice.equals("")){
									JOptionPane.showMessageDialog(Main.this, "Input device","Error",JOptionPane.ERROR_MESSAGE);
									return;
								}
								Device  device = deviceMap.get(strDevice);
								Node node=null;
								if(device==null){
									JOptionPane.showMessageDialog(Main.this, "Input device","Error",JOptionPane.ERROR_MESSAGE);
									return;
								}else if((node=device.getConnectNode())==null){
									JOptionPane.showMessageDialog(Main.this, "This device do not connect other node","Error",JOptionPane.ERROR_MESSAGE);
								}else{
									int count = job.workload/1000;
									
									HashSet<Device> result = node.getAllocateDevices(job, job.type, count);
									if(result.size()==0){
										JOptionPane.showMessageDialog(Main.this, "Impossible execute job","Error",JOptionPane.ERROR_MESSAGE);
										return;
									}else{
										new Thread(new Runnable() {
											@Override
											public void run() {
												// TODO Auto-generated method stub
												try {
													HashSet<DeviceComponent> selectPaints=new HashSet<DeviceComponent>();
													for(Device d : result){
														d.allocateResource(job);
														selectPaints.add((DeviceComponent) paintSelectDeviceMap.get(d));
													}
													for(DeviceComponent comp : selectPaints){
														if(comp!=null)
														comp.select(color,job.workload);
													}
												
													Thread.sleep(job.workload);
													for(Device d : result){
														d.deallocateResource(job);
													}
													
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										}).start();
										
									}
								}
							}
						});
						dialog.pack();
						dialog.setModal(true);
						dialog.setVisible(true);
					}
				}
			});
			
			btnControlNode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub

					JDialog dialog=new JDialog();
					dialog.setResizable(false);
					dialog.setTitle("Control Node");
					
					dialog.setLayout(new BorderLayout());
					{
						JFormattedTextField inputX=new JFormattedTextField(new DecimalFormat("#0"));
						JFormattedTextField inputY=new JFormattedTextField(new DecimalFormat("#0"));
						
						JPanel panel = new JPanel(new BorderLayout());
						JPanel topLayout=new JPanel(new BorderLayout());
						JPanel bottomLayout=new JPanel(new GridLayout(0, 2));
						JPanel rightLayout=new JPanel();
						JComboBox<String> cbNode = new JComboBox<String>();
						for(String node : nodeNames){
							cbNode.addItem(node);
						}
						JCheckBox cbShow=new JCheckBox("show");
						{
							String name=(String) cbNode.getSelectedItem();
							if(name!=null && !name.trim().equals("")){
								boolean value=showNodeMap.get(name);
								cbShow.setSelected(value);
								inputX.setEnabled(value);
								inputY.setEnabled(value);
							}
						}
						topLayout.add(cbNode, BorderLayout.CENTER);
						topLayout.add(cbShow,BorderLayout.EAST);
						
						
						
						cbNode.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								String name=(String) cbNode.getSelectedItem();
								if(name!=null && !name.trim().equals("")){
									boolean value=showNodeMap.get(name);
									cbShow.setSelected(value);
									if(value){
										JComponent comp=paintNodeMap.get(name);
										inputX.setText(comp.getX()+"");
										inputY.setText(comp.getY()+"");
									}else{
										inputX.setText("0");
										inputY.setText("0");
									}
									inputX.setEnabled(value);
									inputY.setEnabled(value);
								}
							}
						});
						cbShow.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								boolean checked=cbShow.isSelected();
								String name=(String) cbNode.getSelectedItem();
								inputX.setEnabled(checked);
								inputY.setEnabled(checked);
								if(checked){
									
									JComponent comp=paintNodeMap.get(name);
									if(comp==null){
										comp=new DeviceComponent(name, 12, 4);
										paintPanel.add(comp);
										paintNodeMap.put(name, comp);
										comp.setBounds(0, 0, 120, 120);
									}else{
										comp.add(comp);
									}
									for(String str : nodeToNodeConnects){
										String otherName=null;
										String strs[]=str.split("/", 2);
										if(name.equals(strs[0])){
											otherName = strs[1];
										}else if(name.equals(strs[1])){
											otherName = strs[0];
										}
										if(otherName!=null){
											boolean otherChecked=showNodeMap.get(otherName);
											if(otherChecked){
												JComponent comp2 = paintNodeMap.get(otherName);
												JComponent comp3=new LineComponent((DeviceComponent)comp, (DeviceComponent)comp2);
												paintNodeToNodeLineMap.put(str, comp3);
												paintPanel.add(comp3);
												comp3.repaint();
											}
										}
									}
									for(String str : nodeToDeviceConnects){
										String deviceName=null;
										String strs[]=str.split("/", 2);
										if(name.equals(strs[0])){
											deviceName=strs[1];
											boolean otherChecked=showDeviceMap.get(deviceName);
											if(otherChecked){
												JComponent comp2 = paintDeviceMap.get(deviceName);
												JComponent comp3=new LineComponent((DeviceComponent)comp, (DeviceComponent)comp2);
												paintNodeToDeviceLineMap.put(str, comp3);
												paintPanel.add(comp3);
												comp3.repaint();
											}
											
										}
									}
								}else{
									JComponent comp=paintNodeMap.get(name);
									if(comp!=null){
										paintNodeMap.remove(name);
										paintPanel.remove(comp);
										
									}
									for(String str : nodeToNodeConnects){
										String otherName=null;
										String strs[]=str.split("/", 2);
										if(name.equals(strs[0])){
											otherName = strs[1];
										}else if(name.equals(strs[1])){
											otherName = strs[0];
										}
										if(otherName!=null){
											boolean otherChecked=showNodeMap.get(otherName);
											if(otherChecked){
												JComponent comp3=paintNodeToNodeLineMap.get(str);
												paintNodeToNodeLineMap.remove(str);
												paintPanel.remove(comp3);
											}
										}
									}
									for(String str : nodeToDeviceConnects){
										String deviceName=null;
										String strs[]=str.split("/", 2);
										if(name.equals(strs[0])){
											deviceName=strs[1];
											boolean otherChecked=showDeviceMap.get(deviceName);
											if(otherChecked){
												JComponent comp3=paintNodeToDeviceLineMap.get(str);
												paintNodeToDeviceLineMap.remove(str);
												paintPanel.remove(comp3);
											}
											
										}
									}
								}
								paintPanel.repaint();
								showNodeMap.put(name, checked);
							}
						});
						
						String name=(String) cbNode.getSelectedItem();
						JComponent comp=paintNodeMap.get(name);
						if(comp!=null){
							inputX.setText(comp.getX()+"");
							inputY.setText(comp.getY()+"");
						}else{
							inputX.setText("0");
							inputY.setText("0");
						}
						bottomLayout.add(new JLabel("input node x position"));
						bottomLayout.add(inputX);
						bottomLayout.add(new JLabel("input node y position"));
						bottomLayout.add(inputY);
						
						JButton button=new JButton("save");
						button.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								String name=(String) cbNode.getSelectedItem();
								boolean checked=showNodeMap.get(name);
								if(checked){
									if(inputX.equals("") || inputY.equals("")){
										JOptionPane.showMessageDialog(Main.this, "Input x, y position","Error",JOptionPane.ERROR_MESSAGE);
										return;
									}
									JComponent comp = paintNodeMap.get(name);
									int x = Integer.parseInt(inputX.getText());
									int y = Integer.parseInt(inputY.getText());
									comp.setBounds(x, y, 120, 120);
									
									for(String str : nodeToNodeConnects){
										String otherName=null;
										String strs[]=str.split("/", 2);
										if(name.equals(strs[0])){
											otherName = strs[1];
										}else if(name.equals(strs[1])){
											otherName = strs[0];
										}
										if(otherName!=null){
											JComponent comp2=paintNodeToNodeLineMap.get(str);
											if(comp2!=null)
												comp2.repaint();
										}
									}
									for(String str : nodeToDeviceConnects){
										String strs[]=str.split("/", 2);
										if(name.equals(strs[0])){
											JComponent comp2=paintNodeToDeviceLineMap.get(str);
											if(comp2!=null)
												comp2.repaint();
										}
									}
								}
							}
						});
						rightLayout.add(button);
						
						panel.add(topLayout,BorderLayout.CENTER);
						panel.add(bottomLayout,BorderLayout.SOUTH);
						panel.add(rightLayout,BorderLayout.EAST);
						dialog.add(panel, BorderLayout.CENTER);
					}
					{
						JPanel panel = new JPanel();
						JButton button = new JButton("close");
						
						button.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								dialog.dispose();
							}
						});
						panel.add(button);
						dialog.add(panel, BorderLayout.SOUTH);
					}
					dialog.pack();
					dialog.setModal(true);
					dialog.setVisible(true);
				}
			});
			btnControlDevice.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
						JDialog dialog=new JDialog();
						dialog.setTitle("Control Device");
						dialog.setResizable(false);
						dialog.setLayout(new BorderLayout());
						{
							JFormattedTextField inputX=new JFormattedTextField(new DecimalFormat("#0"));
							JFormattedTextField inputY=new JFormattedTextField(new DecimalFormat("#0"));
							
							JPanel panel = new JPanel(new BorderLayout());
							JPanel topLayout=new JPanel(new BorderLayout());
							JPanel bottomLayout=new JPanel(new GridLayout(0, 2));
							JPanel rightLayout=new JPanel();
							JComboBox<String> cbNode = new JComboBox<String>();
							for(String node : deviceNames){
								cbNode.addItem(node);
							}
							JCheckBox cbShow=new JCheckBox("show");
							{
								String name=(String) cbNode.getSelectedItem();
								if(name!=null && !name.trim().equals("")){
									boolean value=showDeviceMap.get(name);
									cbShow.setSelected(value);
									inputX.setEnabled(value);
									inputY.setEnabled(value);
								}
							}
							topLayout.add(cbNode, BorderLayout.CENTER);
							topLayout.add(cbShow,BorderLayout.EAST);
							
							
							
							cbNode.addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									// TODO Auto-generated method stub
									String name=(String) cbNode.getSelectedItem();
									if(name!=null && !name.trim().equals("")){
										boolean value=showDeviceMap.get(name);
										cbShow.setSelected(value);
										if(value){
											JComponent comp=paintDeviceMap.get(name);
											inputX.setText(comp.getX()+"");
											inputY.setText(comp.getY()+"");
										}else{
											inputX.setText("0");
											inputY.setText("0");
										}
										inputX.setEnabled(value);
										inputY.setEnabled(value);
									}
								}
							});
							cbShow.addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									// TODO Auto-generated method stub
									boolean checked=cbShow.isSelected();
									String name=(String) cbNode.getSelectedItem();
									inputX.setEnabled(checked);
									inputY.setEnabled(checked);
									if(checked){
										JComponent comp=paintDeviceMap.get(name);
										if(comp==null){
											comp=new DeviceComponent(name, 10, 2);
											paintPanel.add(comp);
											paintDeviceMap.put(name, comp);
											paintSelectDeviceMap.put(deviceMap.get(name), comp);
											comp.setBounds(0, 0, 100, 100);
										}else{
											comp.add(comp);
										}
										for(String str : nodeToDeviceConnects){
											String nodeName=null;
											String strs[]=str.split("/", 2);
											if(name.equals(strs[1])){
												nodeName=strs[0];
												boolean otherChecked=showNodeMap.get(nodeName);
												if(otherChecked){
													JComponent comp2 = paintNodeMap.get(nodeName);
													JComponent comp3=new LineComponent((DeviceComponent)comp2, (DeviceComponent)comp);
													paintNodeToDeviceLineMap.put(str, comp3);
													paintPanel.add(comp3);
													comp3.repaint();
												}
											}
										}
									}else{
										JComponent comp=paintDeviceMap.get(name);
										if(comp!=null){
											paintDeviceMap.remove(name);
											paintSelectDeviceMap.remove(deviceMap.get(name));
											paintPanel.remove(comp);
											for(String str : nodeToDeviceConnects){
												String nodeName=null;
												String strs[]=str.split("/", 2);
												if(name.equals(strs[1])){
													nodeName=strs[0];
													boolean otherChecked=showNodeMap.get(nodeName);
													if(otherChecked){
														JComponent comp3=paintNodeToDeviceLineMap.get(str);
														paintPanel.remove(comp3);
														paintNodeToDeviceLineMap.remove(str);
													}
												}
											}
										}
									}
									paintPanel.repaint();
									showDeviceMap.put(name, checked);
								}
							});
							String name=(String) cbNode.getSelectedItem();
							JComponent comp=paintDeviceMap.get(name);
							if(comp!=null){
								inputX.setText(comp.getX()+"");
								inputY.setText(comp.getY()+"");
							}else{
								inputX.setText("0");
								inputY.setText("0");
							}
							bottomLayout.add(new JLabel("input node x position"));
							bottomLayout.add(inputX);
							bottomLayout.add(new JLabel("input node y position"));
							bottomLayout.add(inputY);
							
							JButton button=new JButton("save");
							button.addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									// TODO Auto-generated method stub
									String name=(String) cbNode.getSelectedItem();
									boolean checked=showDeviceMap.get(name);
									if(checked){
										if(inputX.equals("") || inputY.equals("")){
											JOptionPane.showMessageDialog(Main.this, "Input x, y position","Error",JOptionPane.ERROR_MESSAGE);
											return;
										}
										JComponent comp = paintDeviceMap.get(name);
										int x = Integer.parseInt(inputX.getText());
										int y = Integer.parseInt(inputY.getText());
										
										comp.setBounds(x, y, 100, 100);
										
										for(String str : nodeToDeviceConnects){
											String nodeName=null;
											String strs[]=str.split("/", 2);
											if(name.equals(strs[1])){
												nodeName=strs[0];
												boolean otherChecked=showNodeMap.get(nodeName);
												if(otherChecked){
													JComponent comp3=paintNodeToDeviceLineMap.get(str);
													if(comp3==null){
														JComponent comp2=paintNodeMap.get(nodeName);
														comp3=new LineComponent((DeviceComponent)comp2, (DeviceComponent)comp);
													}
													comp3.repaint();
												}
												
											}
										}
									}
								}
							});
							rightLayout.add(button);
							
							panel.add(topLayout,BorderLayout.CENTER);
							panel.add(bottomLayout,BorderLayout.SOUTH);
							panel.add(rightLayout,BorderLayout.EAST);
							dialog.add(panel, BorderLayout.CENTER);
						}
						{
							JPanel panel = new JPanel();
							JButton button = new JButton("close");
							
							panel.add(button);
							dialog.add(panel, BorderLayout.SOUTH);
							button.addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									// TODO Auto-generated method stub
									dialog.dispose();
								}
							});
						}
						dialog.pack();
						dialog.setModal(true);
						dialog.setVisible(true);
					}
			});
			buttonPanel.add(btnControlNode);
			buttonPanel.add(btnControlDevice);
			buttonPanel.add(btnRequestJob);
			
			
			panel.add(paintPanel,BorderLayout.CENTER);
			panel.add(buttonPanel,BorderLayout.SOUTH);
			tabbedPane.addTab("Map", panel);
		}
		{
			//Device layout
			//디바이스 추가 할 수 잇도록 함
			String types[]={"EdgeDevice","MobileDevice","MECServer","CoreCloud"};
			
			JPanel panel = new JPanel(new BorderLayout());
			String header[]={"Device No.","Name","Type","CPU","RAM","IO"};
			JTable table=new JTable(new DefaultTableModel(header, 0));
			table.setDefaultEditor(Object.class, null);
			JPanel panel2 = new JPanel(new BorderLayout());
			JPanel inputPanel=new JPanel(new GridLayout(0, 2));
			JComponent tfInputs[]=new JComponent[header.length-1];
			
			inputPanel.add(new JLabel(header[1]));
			tfInputs[0]=new JTextField();
			inputPanel.add(tfInputs[0]);
			
			inputPanel.add(new JLabel(header[2]));
			tfInputs[1]=new JComboBox<String>();
			JComboBox<String> cbInput=(JComboBox<String>) tfInputs[1];
			for(String type : types){
				cbInput.addItem(type);
			}
			inputPanel.add(tfInputs[1]);
			
			
			for(int i=3;i<header.length;i++){
				inputPanel.add(new JLabel(header[i]));
				tfInputs[i-1]=new JFormattedTextField(new DecimalFormat("#0"));
				inputPanel.add(tfInputs[i-1]);
			}
			panel2.add(inputPanel,BorderLayout.CENTER);
			JButton btnInput=new JButton("input");
			panel2.add(btnInput,BorderLayout.SOUTH);
			panel.add(panel2,BorderLayout.SOUTH);
			panel.add(new JScrollPane(table),BorderLayout.CENTER);
			btnInput.addActionListener(new ActionListener() {
				private int no = 1;
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String []str=new String[header.length];
				
					for(int i=1;i<str.length;i++){
						if(tfInputs[i-1] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i-1];
							str[i]=comp.getText();
						}else if(tfInputs[i-1] instanceof JComboBox<?>){
							JComboBox comp = (JComboBox) tfInputs[i-1];
							str[i]=(String) comp.getSelectedItem();
						}
						if(str[i].trim().equals("")){
							JOptionPane.showMessageDialog(Main.this, "Input data","Error",JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					str[0]=new String(Integer.toString(no++));
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					
					model.addRow(str);
					for(int i=0;i<tfInputs.length;i++){
						if(tfInputs[i] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i];
							comp.setText("");
						}
						
					}
					if(str[2].equals("EdgeDevice")){
						deviceNames.add(str[1]);
						deviceMap.put(str[1],new EdgeDevice(new Resource(Integer.parseInt(str[3]), Integer.parseInt(str[4]), Integer.parseInt(str[5]))));
					}else if(str[2].equals("MobileDevice")){
						deviceNames.add(str[1]);
						deviceMap.put(str[1],new MobileDevice(new Resource(Integer.parseInt(str[3]), Integer.parseInt(str[4]), Integer.parseInt(str[5])),100,80));
					}else if(str[2].equals("MECServer")){
						deviceNames.add(str[1]);
						deviceMap.put(str[1],new MECServer(new Resource(Integer.parseInt(str[3]), Integer.parseInt(str[4]), Integer.parseInt(str[5]))));
					}else if(str[2].equals("CoreCloud")){
						deviceNames.add(str[1]);
						deviceMap.put(str[1],new CoreCloud(new Resource(Integer.parseInt(str[3]), Integer.parseInt(str[4]), Integer.parseInt(str[5]))));
					}
					
					reflash(cbNodeDeviceConnectDevice, deviceNames);
					showDeviceMap.put(str[1], false);
				}
			});
			tabbedPane.addTab("Device", panel);
		}
		
		{
			//Job layout
			//Job 을 생성하고 Job을 관리하며 추후 리퀘스트를  할수 있도록 함
			JPanel panel = new JPanel(new BorderLayout());
			String header[]={"Job No.","Job Name","Job Type","Required CPU","Required RAM","Required IO","Workload","Description"};
			JTable table=new JTable(new DefaultTableModel(header, 0));
			table.setDefaultEditor(Object.class, null);
			panel.add(new JScrollPane(table),BorderLayout.CENTER);
			
			
			JPanel panel2 = new JPanel(new BorderLayout());
			JPanel inputPanel=new JPanel(new GridLayout(0, 2));
			JComponent tfInputs[]=new JComponent[header.length-1];
			
			inputPanel.add(new JLabel(header[1]));
			tfInputs[0]=new JTextField();
			inputPanel.add(tfInputs[0]);
			
			tfInputs[1]=new JComboBox<String>();
			JComboBox<String> cbInput = (JComboBox<String>) tfInputs[1];
			String types[]={"Normal","CPU","IO","MEMORY"};
			for(String type : types){
				cbInput.addItem(type);
			}
			inputPanel.add(new JLabel(header[2]));
			inputPanel.add(tfInputs[1]);
			
			for(int i=3;i<header.length-1;i++){
				inputPanel.add(new JLabel(header[i]));
				tfInputs[i-1]=new JFormattedTextField(new DecimalFormat("#0"));
				inputPanel.add(tfInputs[i-1]);
			}
			
			inputPanel.add(new JLabel(header[header.length-1]));
			tfInputs[header.length-2]=new JTextField();
			inputPanel.add(tfInputs[header.length-2]);
			
			panel2.add(inputPanel,BorderLayout.CENTER);
			JButton btnInput=new JButton("input");
			panel2.add(btnInput,BorderLayout.SOUTH);
			panel.add(panel2,BorderLayout.SOUTH);
			tabbedPane.addTab("Job", panel);
			btnInput.addActionListener(new ActionListener() {
				private int no = 1;
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String []str=new String[header.length];
				
					for(int i=1;i<str.length;i++){
						if(tfInputs[i-1] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i-1];
							str[i]=comp.getText();
						}else if(tfInputs[i-1] instanceof JComboBox<?>){
							JComboBox comp = (JComboBox) tfInputs[i-1];
							str[i]=(String) comp.getSelectedItem();
						}
						if(str[i].trim().equals("")){
							JOptionPane.showMessageDialog(Main.this, "Input data","Error",JOptionPane.ERROR_MESSAGE);
							return;
						}
						
					}
					str[0]=new String(Integer.toString(no++));
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					
					model.addRow(str);
					for(int i=0;i<tfInputs.length;i++){
						if(tfInputs[i] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i];
							comp.setText("");
						}
						
					}
					jobNames.add(str[1]);
					Type type=null;
					if(str[2].equals("Normal")){
						type=Type.NORMAL;
					}else if(str[2].equals("CPU")){
						type=Type.CPU;
					}else if(str[2].equals("IO")){
						type=Type.IO;
					}else if(str[2].equals("MEMORY")){
						type=Type.MEMORY;
					}
					jobNames.add(str[1]);
					jobMap.put(str[1],new Job(new Resource(Integer.parseInt(str[3]), Integer.parseInt(str[4]), Integer.parseInt(str[5])), Integer.parseInt(str[6]),type));
				}
			});
		}
		{
			//Node layout
			//각 노드를 생성하고, 주변 노드에 연결할 수 있도록 지원
			JPanel panel = new JPanel(new BorderLayout());
			String header[]={"Node No.","Name","Description"};
			JTable table=new JTable(new DefaultTableModel(header, 0));
			table.setDefaultEditor(Object.class, null);
			panel.add(new JScrollPane(table),BorderLayout.CENTER);
			
			
			JPanel panel2 = new JPanel(new BorderLayout());
			JPanel inputPanel=new JPanel(new GridLayout(0, 2));
			JComponent tfInputs[]=new JComponent[header.length-1];
			for(int i=1;i<header.length;i++){
				inputPanel.add(new JLabel(header[i]));
				tfInputs[i-1]=new JTextField();
				inputPanel.add(tfInputs[i-1]);
			}
			
			panel2.add(inputPanel,BorderLayout.CENTER);
			JButton btnInput=new JButton("input");
			panel2.add(btnInput,BorderLayout.SOUTH);
			panel.add(panel2,BorderLayout.SOUTH);
			tabbedPane.addTab("Node", panel);
			btnInput.addActionListener(new ActionListener() {
				private int no = 1;
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String []str=new String[header.length];
				
					for(int i=1;i<str.length;i++){
						if(tfInputs[i-1] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i-1];
							str[i]=comp.getText();
						}else if(tfInputs[i-1] instanceof JComboBox<?>){
							JComboBox comp = (JComboBox) tfInputs[i-1];
							str[i]=(String) comp.getSelectedItem();
						}
						if(str[i].trim().equals("")){
							JOptionPane.showMessageDialog(Main.this, "Input data","Error",JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					str[0]=new String(Integer.toString(no++));
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					
					model.addRow(str);
					for(int i=0;i<tfInputs.length;i++){
						if(tfInputs[i] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i];
							comp.setText("");
						}
					}
					showNodeMap.put(str[1], false);
					nodeNames.add(str[1]);
					nodeMap.put(str[1],new Node());
					reflash(cbNodeDeviceConnectLayoutNode, nodeNames);
					reflash(cbNodeConnectLayoutNode1, nodeNames);
					reflash(cbNodeConnectLayoutNode2, nodeNames);
				}
			});
		}
		{
			//Node Connect layout
			JPanel panel = new JPanel(new BorderLayout());
			String header[]={"Conect No.","Node1 name","Node2 name"};
			JTable table=new JTable(new DefaultTableModel(header, 0));
			table.setDefaultEditor(Object.class, null);
			panel.add(new JScrollPane(table),BorderLayout.CENTER);
			
			JPanel panel2 = new JPanel(new BorderLayout());
			JPanel inputPanel=new JPanel(new GridLayout(0, 2));
			JComponent tfInputs[]=new JComponent[header.length-1];
			for(int i=1;i<header.length;i++){
				inputPanel.add(new JLabel(header[i]));
				if(i==1){
					tfInputs[i-1]=cbNodeConnectLayoutNode1;
				}else{
					tfInputs[i-1]=cbNodeConnectLayoutNode2;
				}
				inputPanel.add(tfInputs[i-1]);
			}
			
			panel2.add(inputPanel,BorderLayout.CENTER);
			JButton btnInput=new JButton("input");
			panel2.add(btnInput,BorderLayout.SOUTH);
			panel.add(panel2,BorderLayout.SOUTH);
			btnInput.addActionListener(new ActionListener() {
				private int no = 1;
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String []str=new String[header.length];
					String beforInput=null;
					for(int i=1;i<str.length;i++){
						if(tfInputs[i-1] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i-1];
							str[i]=comp.getText();
						}else if(tfInputs[i-1] instanceof JComboBox<?>){
							JComboBox comp = (JComboBox) tfInputs[i-1];
							str[i]=(String) comp.getSelectedItem();
						}
							
						if(str[i].trim().equals("")){
							JOptionPane.showMessageDialog(Main.this, "Input data","Error",JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(beforInput==null)
							beforInput=str[i];
						else if(beforInput.equals(str[i])){
							JOptionPane.showMessageDialog(Main.this, "Do not same node","Error",JOptionPane.ERROR_MESSAGE);
							return;
						}else{
							beforInput=str[i];
						}
						
					}
					str[0]=new String(Integer.toString(no++));
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					Node node1=nodeMap.get(str[1]);
					Node node2=nodeMap.get(str[2]);
					node1.addNearDevice(node2);
					node2.addNearDevice(node1);
					nodeToNodeConnects.add(new StringBuilder(str[1]).append('/').append(str[2]).toString());
					model.addRow(str);
					for(int i=0;i<tfInputs.length;i++){
						if(tfInputs[i] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i];
							comp.setText("");
						}
					}
				}
			});
			tabbedPane.addTab("Node connect", panel);
		}
		{
			//Node - Device Connect layout
			JPanel panel = new JPanel(new BorderLayout());
			String header[]={"Conect No.","Node name","Device name"};
			JTable table=new JTable(new DefaultTableModel(header, 0));
			table.setDefaultEditor(Object.class, null);
			panel.add(new JScrollPane(table),BorderLayout.CENTER);
			
			
			JPanel panel2 = new JPanel(new BorderLayout());
			JPanel inputPanel=new JPanel(new GridLayout(0, 2));
			JComponent tfInputs[]=new JComponent[header.length-1];
			for(int i=1;i<header.length;i++){
				inputPanel.add(new JLabel(header[i]));
				if(i==1){
					tfInputs[i-1]=cbNodeDeviceConnectLayoutNode;
				}else{
					tfInputs[i-1]=cbNodeDeviceConnectDevice;
				}
				inputPanel.add(tfInputs[i-1]);
			}
			
			panel2.add(inputPanel,BorderLayout.CENTER);
			JButton btnInput=new JButton("input");
			panel2.add(btnInput,BorderLayout.SOUTH);
			panel.add(panel2,BorderLayout.SOUTH);
			btnInput.addActionListener(new ActionListener() {
				private int no = 1;
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String []str=new String[header.length];
					for(int i=1;i<str.length;i++){
						if(tfInputs[i-1] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i-1];
							str[i]=comp.getText();
						}else if(tfInputs[i-1] instanceof JComboBox<?>){
							JComboBox comp = (JComboBox) tfInputs[i-1];
							str[i]=(String) comp.getSelectedItem();
						}
						if(str[i].trim().equals("")){
							JOptionPane.showMessageDialog(Main.this, "Input data","Error",JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					str[0]=new String(Integer.toString(no++));
					nodeToDeviceConnects.add(new StringBuilder(str[1]).append('/').append(str[2]).toString());
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					Node node=nodeMap.get(str[1]);
					Device device=deviceMap.get(str[2]);
					node.addNearDevice(device);
					model.addRow(str);
					for(int i=0;i<tfInputs.length;i++){
						if(tfInputs[i] instanceof JTextComponent){
							JTextComponent comp = (JTextComponent) tfInputs[i];
							comp.setText("");
						}
					}
				}
			});
			tabbedPane.addTab("Node - Device connect", panel);
		}
		this.add(tabbedPane,BorderLayout.CENTER);
	}
	public static void main(String[] args){
		new Main();
	}
}
