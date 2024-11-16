package Account;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithdrawOrDeposit {

		private CrudAccRepository accRepository;
		
		void withdraw(Integer id, double amount) {
			
		}
}
