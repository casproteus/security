package org.cas.client.platform.bar.model;

/**
 * don't use negative number, because bill uses negative status to indicate the refund money.
 * @author Simon&Nicholas
 *
 */
public interface DBConsts {
	int to_pay = 0;
	int original = 0;
	int completed = 10;
	int suspended = 50;
	int voided = 100;	//voided bull, removed output.
	int deleted = 1000;
}
