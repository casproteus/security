package org.cas.client.platform.bar.model;

/**
 * don't use negative number, because bill uses negative status to indicate the refund money.
 * @author Simon&Nicholas
 *
 */
public interface DBConsts {
	int original = 0;
	int billPrinted = 5;
	int suspended = 8;
	int completed = 10;
	int voided = 100;	//voided bull, removed output.
	int dumpted = 500;	//put it here because it might be more closer to deleted. and may be we can choose to don't show it in check list.
	int deleted = 1000;
}
