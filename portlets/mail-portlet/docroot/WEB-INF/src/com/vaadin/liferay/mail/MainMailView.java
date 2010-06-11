package com.vaadin.liferay.mail;

import java.util.ArrayList;
import java.util.List;

import com.liferay.mail.model.Account;
import com.liferay.mail.model.Message;
import com.liferay.mail.service.AccountLocalServiceUtil;
import com.liferay.mail.service.MessageLocalServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.SplitPanel;

@SuppressWarnings("serial")
public class MainMailView extends CustomComponent {

	private MessageList messageList;
	private Folders folders;
	private MessageView messageView;

	public MainMailView() {

		messageView = new MessageView();
		messageList = new MessageList(this, messageView);
		folders = new Folders(messageList);

		SplitPanel messageListViewSplit =
			new SplitPanel(SplitPanel.ORIENTATION_VERTICAL);
		messageListViewSplit.setSizeFull();
		messageListViewSplit.setSplitPosition(250, SplitPanel.UNITS_PIXELS);
		messageListViewSplit.setFirstComponent(messageList);
		messageListViewSplit.setSecondComponent(messageView);

		SplitPanel split = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
		split.setSplitPosition(170, SplitPanel.UNITS_PIXELS);
		split.setStyleName("small");

		split.addComponent(folders);
		split.addComponent(messageListViewSplit);

		setCompositionRoot(split);
	}

	public void show(Account account) {

		try {
			List<Account> accounts = AccountLocalServiceUtil
					.getAccounts(Controller.get().getUser().getUserId());

			folders.setAccounts(accounts);
		}
		catch (SystemException e) {
			Controller.get().showUnexpectedError(e);
		}

		folders.showInbox(account);

	}

	public void showFirstAccountWithUnreadMessages() {

		List<Account> accounts = new ArrayList<Account>();
		try {
			accounts = AccountLocalServiceUtil.getAccounts(Controller.get()
					.getUser().getUserId());

			folders.setAccounts(accounts);
		}
		catch (SystemException e) {
			Controller.get().showUnexpectedError(e);
		}

		for (final Account account : accounts) {
			try {
				long messages = MessageLocalServiceUtil
						.getAccountUnreadMessagesCount(account.getAccountId());
				if (messages > 0) {
					show(account);
					return;
				}
			} catch (SystemException e) {
				// could not retrieve messages for the account, just skip the
				// account
			}
		}
		show(accounts.get(0));
	}

	public Account getCurrentAccount() {

		return folders.getCurrentAccount();
	}

	public Message getSelectedMessage() {

		return messageList.getSelectedMessage();
	}

	public List<Message> getSelectedMessages() {

		return messageList.getSelectedMessages();
	}
	
	public void update() {
		messageList.refresh();
		folders.refresh();
	}

}
