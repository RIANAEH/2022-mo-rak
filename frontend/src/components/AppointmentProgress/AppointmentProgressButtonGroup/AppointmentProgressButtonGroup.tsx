import { useTheme } from '@emotion/react';
import React, { MouseEventHandler } from 'react';
import Button from '../../common/Button/Button';

interface Props {
  onClickProgress: MouseEventHandler<HTMLButtonElement>;
}

function AppointmentProgressButtonGroup({ onClickProgress }: Props) {
  const theme = useTheme();

  return (
    <Button
      variant="filled"
      colorScheme={theme.colors.PURPLE_100}
      width="31.6rem"
      fontSize="4rem"
      onClick={onClickProgress}
    >
      선택
    </Button>
  );
}

export default AppointmentProgressButtonGroup;