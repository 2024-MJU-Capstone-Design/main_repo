package CoinMerge.coinMergeSpring.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberException extends RuntimeException {
  private final MemberErrorResult errorResult;
}
