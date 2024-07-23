package To_Do_List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ToDoListApp {
	private JFrame frame;
	private DefaultListModel<Task> listModel;
	private JList<Task> taskList;
	private ToDoList toDoList;
	private JTextField titleField;
	private JTextField descriptionField;
	private JTextField dueDateField;
	private List<TaskHistoryEntry> history;

	public ToDoListApp() {
		toDoList = new ToDoList();
		history = new ArrayList<>();
		initialize();
	}

	private void initialize() {
		frame = new JFrame("To-Do List Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 400);
		frame.setLayout(new BorderLayout());

		// Menu bar setup
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("\u22EE"); // Unicode character for vertical ellipsis (three dots)
		JMenuItem historyMenuItem = new JMenuItem("History");
		JMenuItem exitMenuItem = new JMenuItem("Exit");

		historyMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHistory();
			}
		});

		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
				if (confirmed == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		menu.add(historyMenuItem);
		menu.add(exitMenuItem);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);

		listModel = new DefaultListModel<>();
		taskList = new JList<>(listModel);
		taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		taskList.setCellRenderer(new TaskCellRenderer());
		JScrollPane listScrollPane = new JScrollPane(taskList);

		JPanel inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(new JLabel("Title:"), gbc);

		titleField = new JTextField();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		inputPanel.add(titleField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		inputPanel.add(new JLabel("Description:"), gbc);

		descriptionField = new JTextField();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		inputPanel.add(descriptionField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		inputPanel.add(new JLabel("Due Date:"), gbc);

		dueDateField = new JTextField();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		inputPanel.add(dueDateField, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;

		JButton addButton = new JButton("Add Task");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addTask();
			}
		});
		inputPanel.add(addButton, gbc);

		gbc.gridx = 2;
		gbc.gridy = 3;
		JButton completeButton = new JButton("Mark as Complete");
		completeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				markTaskAsComplete();
			}
		});
		inputPanel.add(completeButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		JButton removeButton = new JButton("Remove Task");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeTask();
			}
		});
		inputPanel.add(removeButton, gbc);

		frame.add(listScrollPane, BorderLayout.CENTER);
		frame.add(inputPanel, BorderLayout.SOUTH);
		frame.setVisible(true);
	}

	private void addTask() {
		String title = titleField.getText();
		String description = descriptionField.getText();
		String dueDate = dueDateField.getText();
		Task task = new Task(title, description, dueDate);
		toDoList.addTask(task);
		listModel.addElement(task);
		history.add(new TaskHistoryEntry(task, "Added")); // Add to history
		clearInputFields();
	}

	private void markTaskAsComplete() {
		int selectedIndex = taskList.getSelectedIndex();
		if (selectedIndex != -1) {
			Task task = listModel.getElementAt(selectedIndex);
			toDoList.markTaskAsComplete(selectedIndex);
			task.setComplete(true);
			taskList.repaint();
			history.add(new TaskHistoryEntry(task, "Completed")); // Add to history
		} else {
			JOptionPane.showMessageDialog(frame, "Please select a task to mark as complete.");
		}
	}

	private void removeTask() {
		int selectedIndex = taskList.getSelectedIndex();
		if (selectedIndex != -1) {
			Task task = listModel.getElementAt(selectedIndex);
			toDoList.removeTask(selectedIndex);
			listModel.remove(selectedIndex);
			task.setComplete(false);
			history.add(new TaskHistoryEntry(task, "Deleted")); // Add to history
		} else {
			JOptionPane.showMessageDialog(frame, "Please select a task to remove.");
		}
	}

	private void clearInputFields() {
		titleField.setText("");
		descriptionField.setText("");
		dueDateField.setText("");
	}

	private void showHistory() {
		JDialog historyDialog = new JDialog(frame, "History", true);
		historyDialog.setSize(500, 400);
		historyDialog.setLayout(new BorderLayout());

		JPanel historyPanel = new JPanel();
		historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));

		JLabel addedLabel = new JLabel("Tasks Added:");
		addedLabel.setForeground(Color.BLUE);
		historyPanel.add(addedLabel);

		DefaultListModel<TaskHistoryEntry> addedModel = new DefaultListModel<>();
		JList<TaskHistoryEntry> addedList = new JList<>(addedModel);
		addedList.setCellRenderer(new HistoryTaskCellRenderer());
		JScrollPane addedScrollPane = new JScrollPane(addedList);
		historyPanel.add(addedScrollPane);

		JLabel completedLabel = new JLabel("Tasks Completed:");
		completedLabel.setForeground(Color.GREEN);
		historyPanel.add(completedLabel);

		DefaultListModel<TaskHistoryEntry> completedModel = new DefaultListModel<>();
		JList<TaskHistoryEntry> completedList = new JList<>(completedModel);
		completedList.setCellRenderer(new HistoryTaskCellRenderer());
		JScrollPane completedScrollPane = new JScrollPane(completedList);
		historyPanel.add(completedScrollPane);

		JLabel deletedLabel = new JLabel("Tasks Deleted:");
		deletedLabel.setForeground(Color.RED);
		historyPanel.add(deletedLabel);

		DefaultListModel<TaskHistoryEntry> deletedModel = new DefaultListModel<>();
		JList<TaskHistoryEntry> deletedList = new JList<>(deletedModel);
		deletedList.setCellRenderer(new HistoryTaskCellRenderer());
		JScrollPane deletedScrollPane = new JScrollPane(deletedList);
		historyPanel.add(deletedScrollPane);

		for (TaskHistoryEntry entry : history) {
			if (entry.getAction().equals("Added")) {
				addedModel.addElement(entry);
			} else if (entry.getAction().equals("Completed")) {
				completedModel.addElement(entry);
			} else if (entry.getAction().equals("Deleted")) {
				deletedModel.addElement(entry);
			}
		}

		historyDialog.add(historyPanel, BorderLayout.CENTER);
		historyDialog.setVisible(true);
	}

	private static class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {
		private JLabel titleLabel;
		private JLabel descriptionLabel;
		private JLabel dueDateLabel;
		private JLabel completeLabel;

		public TaskCellRenderer() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			titleLabel = new JLabel();
			descriptionLabel = new JLabel();
			dueDateLabel = new JLabel();
			completeLabel = new JLabel();
			add(titleLabel);
			add(descriptionLabel);
			add(dueDateLabel);
			add(completeLabel);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index, boolean isSelected, boolean cellHasFocus) {
			titleLabel.setText("Title: " + task.getTitle());
			descriptionLabel.setText("Description: " + task.getDescription());
			dueDateLabel.setText("Due Date: " + task.getDueDate());
			completeLabel.setText("Complete: " + (task.getIsComplete() ? "Yes" : "No"));

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			return this;
		}
	}

	private static class HistoryTaskCellRenderer extends JPanel implements ListCellRenderer<TaskHistoryEntry> {
		private JLabel titleLabel;
		private JLabel descriptionLabel;
		private JLabel dueDateLabel;
		private JLabel completeLabel;
		private JLabel actionLabel;

		public HistoryTaskCellRenderer() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			titleLabel = new JLabel();
			descriptionLabel = new JLabel();
			dueDateLabel = new JLabel();
			completeLabel = new JLabel();
			actionLabel = new JLabel();
			add(titleLabel);
			add(descriptionLabel);
			add(dueDateLabel);
			add(completeLabel);
			add(actionLabel);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends TaskHistoryEntry> list, TaskHistoryEntry entry, int index, boolean isSelected, boolean cellHasFocus) {
			Task task = entry.getTask();
			titleLabel.setText("Title: " + task.getTitle());
			descriptionLabel.setText("Description: " + task.getDescription());
			dueDateLabel.setText("Due Date: " + task.getDueDate());
			completeLabel.setText("Complete: " + (task.getIsComplete() ? "Yes" : "No"));
			actionLabel.setText("Action: " + entry.getAction());

			if (entry.getAction().equals("Added")) {
				setBackground(Color.BLUE);
			} else if (entry.getAction().equals("Completed")) {
				setBackground(Color.GREEN);
			} else if (entry.getAction().equals("Deleted")) {
				setBackground(Color.RED);
			}

			return this;
		}
	}

	private static class TaskHistoryEntry {
		private Task task;
		private String action;

		public TaskHistoryEntry(Task task, String action) {
			this.task = task;
			this.action = action;
		}

		public Task getTask() {
			return task;
		}

		public String getAction() {
			return action;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ToDoListApp();
			}
		});
	}
}
